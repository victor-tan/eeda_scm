package controllers.profile;

import interceptor.SetAttrLoginUserInterceptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Office;
import models.ParentOfficeModel;
import models.Toll;
import models.UserOffice;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.log.Logger;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import controllers.util.ParentOffice;
import controllers.util.PermissionConstant;

@RequiresAuthentication
@Before(SetAttrLoginUserInterceptor.class)
public class UnitController extends Controller {
    private Logger logger = Logger.getLogger(UnitController.class);
    Subject currentUser = SecurityUtils.getSubject();
    ParentOfficeModel pom = ParentOffice.getInstance().getOfficeId(this);

    public void searchAllUnit() {
        List<Record> units = Db.find("select * from unit");
        renderJson(units);
    }

    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_LIST })
    public void index() {
        render("/yh/profile/toll/TollList.html");
    }

    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_LIST })
    public void list() {
        String sLimit = "";
        String pageIndex = getPara("sEcho");
        if (getPara("iDisplayStart") != null
                && getPara("iDisplayLength") != null) {
            sLimit = " LIMIT " + getPara("iDisplayStart") + ", "
                    + getPara("iDisplayLength");
        }
        Long parentID = pom.getParentOfficeId();
        // 获取总条数
        String totalWhere = "";
        String sql = "select count(1) total from fin_item  where type ='应收' and office_id = "
                + parentID;
        Record rec = Db.findFirst(sql + totalWhere);
        logger.debug("total records:" + rec.getLong("total"));

        // 获取当前页的数据
        List<Record> orders = Db.find(
                "select * from fin_item  where type ='应收' and office_id = ?",
                parentID);
        Map orderMap = new HashMap();
        orderMap.put("sEcho", pageIndex);
        orderMap.put("iTotalRecords", rec.getLong("total"));
        orderMap.put("iTotalDisplayRecords", rec.getLong("total"));

        orderMap.put("aaData", orders);

        renderJson(orderMap);

    }

    // 编辑条目按钮
    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_CREATE,
            PermissionConstant.PERMSSION_T_UPDATE }, logical = Logical.OR)
    public void Edit() {
        String id = getPara();
        if (id != null) {
            Toll h = Toll.dao.findById(id);
            setAttr("to", h);
            render("/yh/profile/toll/TollEdit.html");
        } else {
            render("/yh/profile/toll/TollEdit.html");
        }
    }

    // 删除条目
    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_DELETE })
    public void delete() {
        String id = getPara();
        if (id != null) {
            Toll l = Toll.dao.findById(id);
            Object obj = l.get("is_stop");
            if (obj == null || "".equals(obj) || obj.equals(false)
                    || obj.equals(0)) {
                l.set("is_stop", true);
            } else {
                l.set("is_stop", false);
            }
            l.update();
        }
        redirect("/toll");
    }

    // 添加编辑保存
    @RequiresPermissions(value = { PermissionConstant.PERMSSION_T_CREATE,
            PermissionConstant.PERMSSION_T_UPDATE }, logical = Logical.OR)
    public void SaveEdit() {

        String id = getPara("id");

        String name = getPara("name");
        String type = "应收";
        String code = getPara("code");
        String remark = getPara("remark");

        Long parentID = pom.getParentOfficeId();

        if (id == "") {
            Toll r = new Toll();

            boolean s = r.set("name", name).set("code", code).set("type", type)
                    .set("office_id", parentID).set("remark", remark).save();
            if (s == true) {
                render("/yh/profile/toll/TollList.html");
                // render("profile/toll/TollList.html");
            }
        } else {
            Toll toll = Toll.dao.findById(id);
            boolean b = toll.set("name", name).set("type", type)
                    .set("code", code).set("remark", remark).update();
            render("/yh/profile/toll/TollList.html");
        }

    }
}
