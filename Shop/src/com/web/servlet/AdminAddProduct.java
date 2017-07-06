package com.web.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.pojo.Category;
import com.pojo.Product;
import com.service.AdminService;
import com.utils.BeanFactory;
import com.utils.CommonUtils;

public class AdminAddProduct extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Product product = new Product();
		
		//收集数据的容器
		Map<String,Object> map = new HashMap<String,Object>();
		
		try {
			//创建磁盘文件项工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			//创建文件上传核心对象
			ServletFileUpload upload = new ServletFileUpload(factory);
			//解析request获得文件项对象集合

			List<FileItem> parseRequest = upload.parseRequest(request);
			for(FileItem item : parseRequest){
				//判断是否是普通表单项
				boolean formField = item.isFormField();
				if(formField){
					//普通表单项 获得表单的数据 封装到Product实体中
					String fieldName = item.getFieldName();
					String fieldValue = item.getString("UTF-8");
					
					map.put(fieldName, fieldValue);
					
				}else{
					//文件上传项 获得文件名称 获得文件的内容
					String fileName = item.getName();
					String path = this.getServletContext().getRealPath("upload");
					InputStream in = item.getInputStream();
					OutputStream out = new FileOutputStream(path+"/"+fileName);
					IOUtils.copy(in, out);
					in.close();
					out.close();
					item.delete();
					map.put("pimage", "upload/"+fileName);
					
				}
				
			}
			
			BeanUtils.populate(product, map);
		
			product.setPid(CommonUtils.getUuid());
			SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
			String dateString=formatter.format(new Date());
			product.setPdate(formatter.parse(dateString));
			
			product.setPflag(0);
			
			Category category = new Category();
			category.setCid(map.get("cid").toString());
			product.setCategory(category);
			
			//将product传递给service层
			AdminService service = (AdminService) BeanFactory.getBean("adminService");
			service.saveProduct(product);
			
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
