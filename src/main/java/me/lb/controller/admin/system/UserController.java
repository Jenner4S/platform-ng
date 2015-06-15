package me.lb.controller.admin.system;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

import me.lb.model.system.Emp;
import me.lb.model.system.User;
import me.lb.service.system.EmpService;
import me.lb.service.system.UserService;
import me.lb.support.jackson.JsonWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
// 依附于员工实现
@RequestMapping(value = "/admin/system/emp")
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private EmpService empService;

	@ResponseBody
	@RequestMapping(value = "/{empId}/user", method = RequestMethod.POST)
	public String user(@PathVariable int empId, String objs) {
		ObjectMapper om = new ObjectMapper();
		try {
			Emp emp = empService.findById(empId);
			List<User> list = om.readValue(objs,
					new TypeReference<List<User>>() {
					});
			// 先完成验证
			for (User temp : list) {
				if (temp.getId() == null) {
					String loginName = temp.getLoginName();
					if (!userService.validate(loginName)) {
						return "{ \"msg\" : \"" + loginName
								+ "与已有用户名冲突，请更换后重试！\" }";
					}
				}
			}
			// 通过验证
			for (User temp : list) {
				// id判断是否存在
				if (temp.getId() != null && temp.getId() > 0) {
					// id存在，有该记录，更新
					// 为了避免更新导致的清空数据，仅处理需要更新的字段
					User obj = userService.findById(temp.getId());
					obj.setLoginName(temp.getLoginName());
					obj.setLoginPass(temp.getLoginPass());
					obj.setEnabled(temp.getEnabled());
					userService.update(obj);
				} else {
					// id不存在，需要存储
					temp.setEmp(emp);
					temp.setCreateDate(new Timestamp(new Date().getTime()));
					userService.save(temp);
				}
			}
			return "{ \"success\" : true }";
		} catch (Exception e) {
			e.printStackTrace();
			return "{ \"msg\" : \"分配失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{empId}/user/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable int empId, @PathVariable int id) {
		try {
			userService.delete(userService.findById(id));
			return "{ \"success\" : true }";
		} catch (Exception e) {
			return "{ \"msg\" : \"删除失败，请确认操作无误后重试，或联系管理员处理！\" }";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{empId}/user/data", method = RequestMethod.GET)
	public String data(@PathVariable int empId) throws Exception {
		// 用于展示某个员工用户列表的查询
		Emp emp = empService.findById(empId);
		Set<User> set = emp.getUsers();
		return JsonWriter.getInstance().filter(User.class, "emp", "roles")
				.getWriter().writeValueAsString(set);
	}

}
