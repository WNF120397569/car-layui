package com.yimin.carlayui.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yimin.carlayui.common.CarStatus;
import com.yimin.carlayui.common.Const;
import com.yimin.carlayui.common.OrderStatus;
import com.yimin.carlayui.common.Result;
import com.yimin.carlayui.entity.Car;
import com.yimin.carlayui.entity.Order;
import com.yimin.carlayui.entity.User;
import com.yimin.carlayui.service.CarService;
import com.yimin.carlayui.service.OrderService;
import com.yimin.carlayui.service.UserService;
import com.yimin.carlayui.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CarService carService;

    @Autowired
    private UserService userService;

    /**
     * 车辆预定
     * @param map
     * @param session
     * @return
     */
    @PostMapping(value = "/submitOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result submitOrder(@RequestBody Map<String, String> map, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("请先登陆");
        }

        String id = map.get("id");
        Car car = carService.getById(id);
        if (car.getStatus() != 1) {
            return Result.error("当前状态不允许预定");
        }
        Order o = orderService.getEffectiveOrder(Long.parseLong(id));
        if (o != null) {
            //已存在订单
            return Result.error("当前车辆正在出租中");
        }
        String startDate = LocalDate.now().toString();
        String endDate = map.get("enddate");
        long days = DateUtil.daysBetween(startDate, endDate);
        if (days < Const.MIN_DAYS) {
            return Result.error("最少租" + Const.MIN_DAYS + "天");
        }

        //创建订单
        Order order = new Order();
        order.setCreateTime(new Date());
        if(user.getId().equals(car.getUserId())){
            return Result.error("暂时不能预定自己的车~");
        }
        order.setCustomerId(user.getId());
        order.setOwnerId(car.getUserId());
        order.setCarId(car.getId());
        order.setStatus(OrderStatus.NOT_AGREEMENT.getValue());
        order.setRent(car.getRent());
        order.setDeposit(car.getDeposit());
        order.setDays(days);
        order.setTotal((int) (car.getRent() * days));
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
            order.setStartDate(start);
            order.setEndDate(end);
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.error("日期格式有误");
        }

        orderService.save(order);

        // System.out.println(order.getId());

        //返回订单的id
        return Result.success("订单创建成功，请签订合同", order.getId());
    }


    /**
     * 查看合同信息
     */
    @GetMapping("/order/agreement/view")
    public String agreementView(@RequestParam("orderId") Long id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "error/404";
        }
        Order order = orderService.getById(id);
        if (order == null) {
            return "error/404";
        } else {
            Car car = carService.getById(order.getCarId());
            User owner = userService.getById(order.getOwnerId());
            User customer = userService.getById(order.getCustomerId());
            model.addAttribute("car", car);
            model.addAttribute("owner", owner);
            model.addAttribute("customer", customer);
            model.addAttribute("order", order);
            return "agreement";
        }

    }

    /**
     * 签订合同 其实就是改一下订单状态
     *
     * @param orderId
     * @return
     */
    @PostMapping(value = "/confirmAgreement", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result confirmAgreement(@RequestBody String orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        try {
            order.setStatus(OrderStatus.NOT_PAY.getValue());
            orderService.saveOrUpdate(order);
            return Result.success("已同意，3s后将转向支付页面", orderId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器发生错误");
        }

    }

    /**
     * 转到支付页面
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("/order/pay")
    public String orderPay(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return "error/404";
        }
        try {
            Car car = carService.getById(order.getCarId());
            model.addAttribute("car", car);
            model.addAttribute("order", order);
            return "pay";
        } catch (Exception e) {
            e.printStackTrace();
            return "error/5xx";
        }
    }

    @PostMapping(value = "/submitPay", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result submitPay(@RequestBody String orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        } else {
            try {
                order.setStatus(OrderStatus.NORMAL.getValue());
                orderService.saveOrUpdate(order);
                //将car设置为已租出
                Car car = carService.getById(order.getCarId());
                car.setStatus(CarStatus.HAS_RENT);
                carService.saveOrUpdate(car);
                return Result.success("支付成功，联系车主提车吧");
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error("发生错误");
            }
        }

    }

    /**
     * 跳转到订单管理页面
     */
    @GetMapping("/carOrderPage")
    public String carOrder(Model model) {
        model.addAttribute("isOrderPage", true);
        return "car_order";
    }


    /**
     * 订单表格数据查询接口
     * 除page外的参数是过滤条件
     */
    @GetMapping("/carOrderList")
    @ResponseBody
    public Map<String, Object> carOrder(@RequestParam(value = "page", required = false, defaultValue = "1") Long cur,
                                        @RequestParam(value = "canceled",required = false) String canceled,
                                        @RequestParam(value = "not_agreement",required = false) String not_agreement,
                                        @RequestParam(value = "not_pay",required = false) String not_pay,
                                        @RequestParam(value = "normal",required = false) String normal,
                                        @RequestParam(value = "finish",required = false) String finish,
                                        @RequestParam(value = "end_rent_apply",required = false) String end_rent_apply,
                                        @RequestParam(value = "end_rent_not_allow",required = false) String end_rent_not_allow,
                                        HttpSession session, Model model) {
        IPage<Order> orderPage;
        Page<Order> page;
        //如果没有传参，默认为第一页
        if (cur == null) {
            page = new Page<>(1, 6);//分页
        } else {
            page = new Page<>(cur, 6);
        }

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        List<Integer> list = new ArrayList<>();
        if(StringUtils.isNotEmpty(canceled)){
            log.debug("canceled="+canceled);
            list.add(-3);
        }
        if(StringUtils.isNotEmpty(not_agreement)){
            log.debug("not_agreement="+not_agreement);
            list.add(-2);
        }
        if(StringUtils.isNotEmpty(not_pay)){
            log.debug("not_pay="+not_pay);
            list.add(-1);
        }
        if(StringUtils.isNotEmpty(normal)){
            log.debug("normal="+normal);
            list.add(0);
        }
        if(StringUtils.isNotEmpty(finish)){
            log.debug("finish="+finish);
            list.add(1);
        }
        if(StringUtils.isNotEmpty(end_rent_apply)){
            log.debug("end_rent_apply="+end_rent_apply);
            list.add(2);
        }
        if(StringUtils.isNotEmpty(end_rent_not_allow)){
            log.debug("end_rent_not_allow="+end_rent_not_allow);
            list.add(3);
        }
        if(list.size() >0){
            wrapper.in("status",list);
        }



        //TODO 已经到期的订单做相应的处理

        //返回不同角色的订单 已做处理
        User user = (User) session.getAttribute("user");
        if ("admin".equals(user.getRole())) {
            //每个人都要过滤status条件
            orderPage = orderService.page(page,wrapper);
        } else if ("owner".equals(user.getRole())) {
            wrapper.eq("owner_id", user.getId());
            orderPage = orderService.page(page, wrapper);
        } else {
            //customer
            wrapper.eq("customer_id", user.getId());
            orderPage = orderService.page(page, wrapper);
        }

        //layui表格要求返回的格式
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", orderPage.getTotal());
        map.put("data", orderPage.getRecords());

        return map;
    }

    @PostMapping(value = "/cancelOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result cancelOrder(@RequestBody String orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("没有权限");
        }
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        } else {
            Integer status = order.getStatus();
            //在未付款或未同意合约时，可以取消
            if (status == OrderStatus.NOT_PAY.getValue() || status == OrderStatus.NOT_AGREEMENT.getValue()) {
                try {
                    order.setStatus(OrderStatus.CANCEL.getValue());
                    orderService.updateById(order);
                    return Result.success("取消订单成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("服务器发生错误，取消订单失败");
                }
            }
            return Result.error("当前状态不允许取消");
        }

    }


    /**
     * 退租申请
     *
     * @param orderId
     * @param session
     * @return
     */
    @PostMapping(value = "/endOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result endOrder(@RequestBody String orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("没有权限");
        }
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        } else {
            Integer status = order.getStatus();
            //在生效中或拒绝申请时，可以申请退租
            if (status == OrderStatus.NORMAL.getValue() || status == OrderStatus.END_RENT_NOT_ALLOW.getValue()) {
                try {
                    order.setStatus(OrderStatus.END_RENT_APPLY.getValue());
                    orderService.updateById(order);
                    return Result.success("申请退租成功，请等待审核");
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("服务器发生错误，取消订单失败");
                }
            }
            return Result.error("当前状态不允许申请退租");
        }

    }



    /**
     * 取消退租申请
     *
     * @param orderId
     * @param session
     * @return
     */
    @PostMapping(value = "/cancelEndOrder", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result cancelEndOrder(@RequestBody String orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("没有权限");
        }
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        } else {
            Integer status = order.getStatus();
            //在退租中时，可以取消，回复正常
            if (status == OrderStatus.END_RENT_APPLY.getValue()) {
                try {
                    order.setStatus(OrderStatus.NORMAL.getValue());
                    orderService.updateById(order);
                    return Result.success("取消退租成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("服务器发生错误，取消失败");
                }
            }
            return Result.error("当前状态不允许取消退租申请");
        }

    }


    /**
     * 处理退租申请
     */
    @PostMapping(value = "/endOrderHandle", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Result endOrderHandle(@RequestBody Map<String, Object> map, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Result.error("没有权限");
        }
        int orderId = (int) map.get("orderId");
        int tag = (int) map.get("tag");
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error("订单不存在");
        } else if (order.getStatus() != OrderStatus.END_RENT_APPLY.getValue()) {
            return Result.error("当前状态不是申请退租，无法同意或拒绝");
        } else {
            //在生效中或拒绝申请时，可以申请退租
            if (tag==1) {
                try {
                    // 同意退租，将状态改为到期
                    order.setStatus(OrderStatus.FINISH.getValue());
                    // 更新订单的结束日期和金额
                    Date date = new Date();
                    order.setEndDate(date);
                    long days = DateUtil.daysBetween(order.getStartDate(), date);
                    order.setDays(days);
                    order.setTotal((int) (order.getRent()*days+order.getDeposit()));
                    orderService.updateById(order);
                    //更改车辆状态
                    Car car = carService.getById(order.getCarId());
                    if(car!=null &&car.getStatus()==CarStatus.HAS_RENT){
                        car.setStatus(CarStatus.NOT_RENT);//将汽车改为未出租
                        carService.saveOrUpdate(car);
                    }
                    return Result.success("已同意退租申请，请及时还车");
                } catch (Exception e) {
                    e.printStackTrace();
                    return Result.error("服务器发生错误，取消订单失败");
                }
            }else{
                order.setStatus(OrderStatus.END_RENT_NOT_ALLOW.getValue());
                orderService.updateById(order);
                return Result.success("已拒绝退租申请");
            }

        }
    }
}


