package me.lb.model.system;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;

@Entity
@Table(name = "ng_sys_user")
// 加入该注解，动态过滤属性
@JsonFilter("me.lb.model.system.User")
public class User implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -418893545923899509L;
	private Integer id;
	private Emp emp;
	private String loginName;
	private String loginPass;
	private Integer enabled;
	private Timestamp createDate;
	private Integer loginRange;
	private Set<Role> roles = new HashSet<Role>(0);

	// 用于反序列化json，存储关联的角色信息
	private String roleIds;

	// Constructors

	/** default constructor */
	public User() {
	}

	/** minimal constructor */
	public User(String loginName, String loginPass) {
		this.loginName = loginName;
		this.loginPass = loginPass;
	}

	/** full constructor */
	public User(Emp emp, String loginName, String loginPass, Integer enabled,
			Timestamp createDate, Integer loginRange, Set<Role> roles) {
		this.emp = emp;
		this.loginName = loginName;
		this.loginPass = loginPass;
		this.enabled = enabled;
		this.createDate = createDate;
		this.loginRange = loginRange;
		this.roles = roles;
	}

	// Property accessors
	@Id
	@GeneratedValue
	@Column(name = "id")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	// 获得用户的时候，希望能直接获取员工，不存在1+n问题
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "empId")
	public Emp getEmp() {
		return this.emp;
	}

	public void setEmp(Emp emp) {
		this.emp = emp;
	}

	@Column(name = "loginName", nullable = false)
	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@Column(name = "loginPass", nullable = false)
	public String getLoginPass() {
		return this.loginPass;
	}

	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}

	@Column(name = "enabled")
	public Integer getEnabled() {
		return this.enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	@Column(name = "createDate", length = 19)
	public Timestamp getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@Column(name = "loginRange")
	public Integer getLoginRange() {
		return this.loginRange;
	}

	public void setLoginRange(Integer loginRange) {
		this.loginRange = loginRange;
	}

	@ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@JoinTable(name = "ng_sys_user_role", joinColumns = { @JoinColumn(name = "userId") }, inverseJoinColumns = { @JoinColumn(name = "roleId") })
	public Set<Role> getRoles() {
		Set<Role> set = new TreeSet<Role>(new Comparator<Role>() {
			@Override
			public int compare(Role o1, Role o2) {
				return o1.getId() - o2.getId();
			}
		});
		set.addAll(this.roles);
		return set;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Transient
	public String getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(String roleIds) {
		this.roleIds = roleIds;
	}

}