package controllers.profile;import interceptor.SetAttrLoginUserInterceptor;import java.util.HashMap;import java.util.List;import java.util.Map;import models.ParentOfficeModel;import models.Role;import models.RolePermission;import models.UserRole;import org.apache.shiro.SecurityUtils;import org.apache.shiro.authz.annotation.RequiresAuthentication;import org.apache.shiro.authz.annotation.RequiresPermissions;import org.apache.shiro.subject.Subject;import com.jfinal.aop.Before;import com.jfinal.core.Controller;import com.jfinal.log.Logger;import com.jfinal.plugin.activerecord.Db;import com.jfinal.plugin.activerecord.Record;import controllers.yh.util.ParentOffice;import controllers.yh.util.PermissionConstant;@RequiresAuthentication@Before(SetAttrLoginUserInterceptor.class)public class RoleController extends Controller {	private Logger logger = Logger.getLogger(RoleController.class);	Subject currentUser = SecurityUtils.getSubject();	ParentOfficeModel pom = ParentOffice.getInstance().getOfficeId(this);		@RequiresPermissions(value = {PermissionConstant.PERMSSION_R_LIST})	public void index() {		render("/yh/profile/role/RoleList.html");	}	@RequiresPermissions(value = {PermissionConstant.PERMSSION_R_LIST})	public void list() {		String sLimit = "";		String pageIndex = getPara("sEcho");		if (getPara("iDisplayStart") != null		        && getPara("iDisplayLength") != null) {			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "			        + getPara("iDisplayLength");		}		// 获取总条数		String totalWhere = "";		String sql = "";		String querySQL = "";		Long parentID = pom.getParentOfficeId();		sql = "select count(1) total from role where office_id is null or office_id = "+ parentID;		querySQL ="select * from role where office_id is null or office_id = "+ parentID;						// 获取当前页的数据		Record rec = Db.findFirst(sql + totalWhere);		logger.debug("total records:" + rec.getLong("total"));				// 获取当前页的数据		List<Record> orders = Db.find(querySQL);		Map orderMap = new HashMap();		orderMap.put("sEcho", pageIndex);		orderMap.put("iTotalRecords", rec.getLong("total"));		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));		orderMap.put("aaData", orders);		renderJson(orderMap);	}	//没有系统管理员	public void listPart() {		String sLimit = "";		String pageIndex = getPara("sEcho");		if (getPara("iDisplayStart") != null		        && getPara("iDisplayLength") != null) {			sLimit = " LIMIT " + getPara("iDisplayStart") + ", "			        + getPara("iDisplayLength");		}		//获取总公司的ID		Long parentID = pom.getParentOfficeId();		// 获取总条数		String totalWhere = "";		String sql = "select count(1) total from role where code != 'admin' and (office_id is null or office_id = " + parentID +")";		Record rec = Db.findFirst(sql + totalWhere);		logger.debug("total records:" + rec.getLong("total"));		// 获取当前页的数据		List<Record> orders = Db.find("select * from role where code != 'admin' and (office_id is null or office_id = ?)",parentID);		Map orderMap = new HashMap();		orderMap.put("sEcho", pageIndex);		orderMap.put("iTotalRecords", rec.getLong("total"));		orderMap.put("iTotalDisplayRecords", rec.getLong("total"));		orderMap.put("aaData", orders);		renderJson(orderMap);	}		// 点击创建角色保存	@RequiresPermissions(value = {PermissionConstant.PERMSSION_R_CREATE})	public void SaveRole() {						Long parentID = pom.getParentOfficeId();		Role r = new Role();		String name = getPara("rolename");		String remark = getPara("roleremark");		String code = getPara("rolecode");		r.set("name", name).set("code", code).set("remark", remark)		.set("office_id", parentID).save();		render("/yh/profile/role/RoleList.html");		/*Date time = Calendar.getInstance().getTime();*/	}	// 点击编辑保存	@RequiresPermissions(value = {PermissionConstant.PERMSSION_R_UPDATE})	public void editRole() {		String id = getPara("id");		String rolename = getPara("rolename");		String remark = getPara("roleremark");		/*Date uptime = Calendar.getInstance().getTime();*/		Role role = Role.dao.findById(id);		role.set("name", rolename).set("remark", remark).update();		render("/yh/profile/role/RoleList.html");	}	public void ClickRole() {	    String id = getPara("id");	    if (id != null) {	        Role h = Role.dao.findById(id);	        setAttr("hh", h);	        render("/yh/profile/role/RoleEdit.html");	    }	}	// 删除	@RequiresPermissions(value = {PermissionConstant.PERMSSION_R_DELETE})	public void deleteRole() {		String id = getPara();		if (id != null) {			Role l = Role.dao.findById(id);			List<UserRole> ulist = UserRole.dao.find("select * from user_role where role_code =?",l.get("code"));			if(ulist.size()>0){				for (UserRole userRole : ulist) {					userRole.delete();				}			}			List<RolePermission> rlist = RolePermission.dao.find("select * from role_permission where role_code = ?",l.get("code"));			if(rlist.size()>0){				for (RolePermission rolePermission : rlist) {					rolePermission.delete();				}			}						l.delete();		}				redirect("/role");	}	public void checkRoleNameExit(){		String name = getPara("rolename");		boolean isExit;		Long parentID = pom.getParentOfficeId();		Role role = Role.dao.findFirst("select * from role where name=? and (office_id is null or office_id = ?)",name,parentID);				if(role==null){			isExit=true;		}else{			isExit=false;		}		renderJson(isExit);	}	public void checkRoleCodeExit(){		String code = getPara("rolecode");		boolean isExit;		Long parentID = pom.getParentOfficeId();		Role role = Role.dao.findFirst("select * from role where code=? and (office_id is null or office_id = ?)",code,parentID);				if(role==null){			isExit=true;		}else{			isExit=false;		}		renderJson(isExit);	}}