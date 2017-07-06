package com.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import redis.clients.jedis.Jedis;

import com.google.gson.Gson;
import com.mchange.v2.c3p0.impl.NewPooledConnection;
import com.pojo.Cart;
import com.pojo.CartItem;
import com.pojo.Category;
import com.pojo.Order;
import com.pojo.OrderItem;
import com.pojo.PageBean;
import com.pojo.Product;
import com.pojo.User;
import com.service.CategoryService;
import com.service.ProductService;
import com.sun.mail.imap.protocol.Item;
import com.utils.CommonUtils;
import com.utils.JedisPoolUtils;
import com.utils.PaymentUtil;

public class ProductServlet extends BaseServlet {
	public void index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductService productService=new ProductService();
		
		List<Product> hotProducts=productService.hotProducts();
		
		List<Product> newProducts=productService.newProducts();
		
		request.setAttribute("hotProducts", hotProducts);
		
		request.setAttribute("newProducts", newProducts);
		
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}
	public void productList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String cid=request.getParameter("cid");
		String currentPageString=request.getParameter("currentPage");
		int currentPage=1;
		if(currentPageString!=null){
			currentPage=Integer.parseInt(currentPageString);
		}
		
		int currentCount=12;
		ProductService productService=new ProductService();
		PageBean pageBean=productService.pageProducts(cid, currentPage, currentCount);
		
		ArrayList<Product> historyProductList=new ArrayList<Product>();
		
		Cookie[] cookies=request.getCookies();
		for(Cookie cookie:cookies){
			if(cookie.getName().equals("pids")){
				String pids=cookie.getValue();
				String[] split=pids.split("-");
				for(String pid:split){
					Product product=productService.selectProductById(pid);
					historyProductList.add(product);
				}
			}
		}
		request.setAttribute("cid", cid);
		request.setAttribute("pagebean",pageBean);
		request.setAttribute("historyProduct",historyProductList);
		request.getRequestDispatcher("/product_list.jsp").forward(request, response);
	}
	public void productInfo(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pid=request.getParameter("pid");
		String cid=request.getParameter("cid");
		String currentPage=request.getParameter("currentPage");
		ProductService productService=new ProductService();
		Product product=productService.selectProductById(pid);
		
		String pids=pid;
		Cookie[] cookies=request.getCookies();
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if(cookie.getName().equals("pids")){
					String[] splitstring=cookie.getValue().split("-");
					List<String> aslist=Arrays.asList(splitstring);
					LinkedList<String> list=new LinkedList<String>(aslist);
					if(list.contains(pid)){
						list.remove(pid);
						list.addFirst(pid);
					}else{
						list.addFirst(pid);
					}
					StringBuffer buffer=new StringBuffer();
					for(int i=0;i<list.size()&&i<7;i++){
						buffer.append(list.get(i));
						buffer.append("-");
					}
					
					pids=buffer.substring(0, buffer.length()-1);
				}
			}
		}
		Cookie pids_cookCookie=new Cookie("pids", pids);
		response.addCookie(pids_cookCookie);
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("product", product);
		request.getRequestDispatcher("/product_info.jsp").forward(request, response);
	}
	public void categoryList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Jedis jedis=JedisPoolUtils.getJedis();
		String jsonString=jedis.get("jsonString");
		if(jsonString==null){
			System.out.println("没有缓存数据");
			CategoryService categoryService=new CategoryService();
			List<Category> categoryList=categoryService.CategoryList();
			Gson gson=new Gson();
			jsonString=gson.toJson(categoryList);
			jedis.set("jsonString", jsonString);
			jedis.close();
		}
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(jsonString);
	}
	
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session=(HttpSession) request.getSession();
		String pid=request.getParameter("pid");
		ProductService productService=new ProductService();
		CartItem cartItem=new CartItem();
		Product product=productService.selectProductById(pid);
		cartItem.setProduct(product);
		int number=Integer.parseInt(request.getParameter("number"));
		cartItem.setNumber(number);
		double subtotal=product.getShop_price()*number;
		cartItem.setSubtotal(subtotal);
		Cart cart=(Cart) session.getAttribute("cart");
		if(cart==null){
			cart=new Cart();
		}
		Map<String, CartItem> cartItems=cart.getCartItems();
		if(cartItems.containsKey(pid)){
			CartItem temCartItem=cartItems.get(pid);
			int oldNumber=temCartItem.getNumber();
			int newNumber=oldNumber+number;
			temCartItem.setNumber(newNumber);
			temCartItem.setSubtotal(newNumber*product.getShop_price());
			cartItems.put(pid,temCartItem);
			
		}else{
			cartItems.put(pid,cartItem);
		}
		Double total=cart.getTotal()+subtotal;
		cart.setTotal(total);
		session.setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pid=request.getParameter("pid");
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		if(cart!=null){
			double tatal=cart.getTotal();
			double subtotal=cart.getCartItems().get(pid).getSubtotal();
			cart.setTotal(tatal-subtotal);
			cart.getCartItems().remove(pid);
		}
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	public void clearCart(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session=request.getSession();
		session.removeAttribute("cart");
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	public void submitOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, ParseException {
		HttpSession session=request.getSession();
		User user=(User) session.getAttribute("user");
		if(user==null){
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		Cart cart=(Cart) session.getAttribute("cart");
		Map<String, CartItem> cartItems=cart.getCartItems();
		
		Order order=new Order();
		List<OrderItem> orderItems=new ArrayList<OrderItem>();
		order.setOid(CommonUtils.getUuid());
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		String dateString=format.format(new Date());
		order.setOrdertime(format.parse(dateString));
		order.setTotal(cart.getTotal());
		order.setState(0);
		order.setAddress(null);
		order.setName(null);
		order.setTelephone(null);
		order.setUser(user);
		
		for(Map.Entry<String, CartItem> entry : cartItems.entrySet()){
			CartItem cartItem = entry.getValue();
			OrderItem orderItem = new OrderItem();
			orderItem.setItemid(CommonUtils.getUuid());
			orderItem.setCount(cartItem.getNumber());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setProduct(cartItem.getProduct());
			orderItem.setOrder(order);
			order.getOrderItems().add(orderItem);
		}
		ProductService productService = new ProductService();
		productService.submitOrder(order);


		session.setAttribute("order", order);

		//页面跳转
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");

	}
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String[]> properties = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, properties);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProductService productService = new ProductService();
		productService.updateOrderAdrr(order);
		
		String orderid = request.getParameter("oid");
		//String money = order.getTotal()+"";//支付金额
		String money = "0.01";//支付金额
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);


		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
				"&p0_Cmd="+p0_Cmd+
				"&p1_MerId="+p1_MerId+
				"&p2_Order="+p2_Order+
				"&p3_Amt="+p3_Amt+
				"&p4_Cur="+p4_Cur+
				"&p5_Pid="+p5_Pid+
				"&p6_Pcat="+p6_Pcat+
				"&p7_Pdesc="+p7_Pdesc+
				"&p8_Url="+p8_Url+
				"&p9_SAF="+p9_SAF+
				"&pa_MP="+pa_MP+
				"&pr_NeedResponse="+pr_NeedResponse+
				"&hmac="+hmac;

		//重定向到第三方支付平台
		response.sendRedirect(url);
	}
	public void myorder(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session=request.getSession();
		User user=(User) session.getAttribute("user");
		if(user==null){
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		List<Order> orders=new ArrayList<Order>();
		ProductService productService=new ProductService();
		orders=productService.findOrdersByUser(user.getUid());
		if(orders!=null){
			for(Order order:orders){
				List<Map<String, Object>> mapList=productService.findOrderItemsByOrder(order.getOid());
				for(Map<String, Object> map:mapList){
					OrderItem orderItem=new OrderItem();
					Product product=new Product();
					try {
						BeanUtils.populate(orderItem, map);
						BeanUtils.populate(product, map);
						orderItem.setProduct(product);
						order.getOrderItems().add(orderItem);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		request.setAttribute("orders",orders);
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
	}
}
