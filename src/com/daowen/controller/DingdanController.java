package com.daowen.controller;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.daowen.dto.Shopcart;
import com.daowen.dto.ShopcartItem;
import com.daowen.entity.Comment;
import com.daowen.entity.Dingdan;
import com.daowen.entity.Dingdanitems;
import com.daowen.entity.Huiyuan;
import com.daowen.entity.Hytype;
import com.daowen.entity.Shangpin;
import com.daowen.service.CommentService;
import com.daowen.service.DingdanService;
import com.daowen.service.DingdanitemsService;
import com.daowen.service.HuiyuanService;
import com.daowen.service.HytypeService;
import com.daowen.service.ShangpinService;
import com.daowen.service.ShopcartService;
import com.daowen.ssm.simplecrud.SimpleController;
import com.daowen.util.SequenceUtil;
import com.daowen.webcontrol.PagerMetal;

@Controller
public class DingdanController extends SimpleController {
	@Override
	@RequestMapping("/admin/dingdanmanager.do")
	public void mapping(HttpServletRequest request, HttpServletResponse response) {

		mappingMethod(request, response);

	}

	public void cancel() {

		String ddid = request.getParameter("ddid");
		if (ddid != null) {
			Dingdan dingdan = dingdanSrv.load("where id=" + ddid);
			dingdan.setState(5);
			dingdanSrv.update(dingdan);
		}
		String forwardurl = request.getParameter("forwardurl");
		redirect(forwardurl);

	}

	public void qianshou() {

		String ddid = request.getParameter("ddid");
		String des = request.getParameter("des");
		String commentren = request.getParameter("commentren");
		String photo = request.getParameter("photo");
		String defen = request.getParameter("defen");
		if (ddid == null)
			return;

		Dingdan dingdan = dingdanSrv.load("where id=" + ddid);
		dingdan.setState(4);
		dingdanSrv.update(dingdan);
		List<Dingdanitems> itemlist = dditemsSrv.getEntity("where ddno='" + dingdan.getDdno() + "'");
		if (itemlist != null) {
			for (Dingdanitems temitem : itemlist) {
				Comment comment = new Comment();
				comment.setBelongid(String.valueOf(temitem.getSpid()));
				comment.setCommentcontent(des == null ? "" : des);
				comment.setXtype("shangpin");
				comment.setPhoto(photo);
				comment.setCommenttime(new Date());
				comment.setCommentren(commentren);
				if (defen != null)
					comment.setDefen(Integer.parseInt(defen));

				commentSrv.save(comment);
			}
		}

		String forwardurl = request.getParameter("forwardurl");
		redirect(forwardurl);
	}

	public void fahuo() {

		String ddid = request.getParameter("ddid");
		String fahuoren = request.getParameter("fahuoren");
		String wltype = request.getParameter("wltype");
		String wlorderno = request.getParameter("wlorderno");

		if (ddid != null) {
			Dingdan dingdan = dingdanSrv.load("where id=" + ddid);
			List<Dingdanitems> itemlist = (List<Dingdanitems>) dditemsSrv
					.getEntity("where ddno='" + dingdan.getDdno() + "'");
			if (itemlist != null) {
				for (Dingdanitems temitem : itemlist) {
					Shangpin temShangpin = shangpinSrv.load("where id=" + temitem.getSpid());
					if (temShangpin != null) {
						temShangpin.setKucun(temShangpin.getKucun() - temitem.getShuliang());
						shangpinSrv.update(temShangpin);
					}
				}
			}

			dingdan.setState(3);
			dingdan.setFahuoren(fahuoren);
			dingdan.setWlorderno(wlorderno == null ? "" : wlorderno);
			dingdan.setWltype(wltype == null ? "" : wltype);
			dingdan.setFahuotime(new Date());
			dingdanSrv.update(dingdan);

		}
		String forwardurl = request.getParameter("forwardurl");
		redirect(forwardurl);

	}

	public void payfor() {

		String ddid = request.getParameter("ddid");
		String accountname = request.getParameter("accountname");
		String paypwd = request.getParameter("paypwd");

		String errorurl = request.getParameter("errorurl");
		if (ddid == null || accountname == null)
			return;

		Huiyuan hy = huiyuanSrv.load("where accountname='" + accountname + "'");
		if ((paypwd != null && !paypwd.equals(hy.getPaypwd())) || paypwd == null) {
			request.setAttribute("errormsg", "<label class='error'>????????????????????????????????????</label>");
			forward(errorurl);
			return;
		}
		Dingdan dingdan = dingdanSrv.load("where id=" + ddid);

		if (hy.getYue() < dingdan.getTotalprice()) {

			request.setAttribute("errormsg", "<label class='error'>?????????????????????????????????,?????????</label>");
			forward(errorurl);
			return;
		}

		hy.setYue((float) (hy.getYue() - dingdan.getTotalprice()));
		huiyuanSrv.update(hy);
		dingdan.setState(2);
		dingdanSrv.update(dingdan);
		request.getSession().setAttribute("huiyuan", hy);

		String forwardurl = request.getParameter("forwardurl");
		redirect(forwardurl);

	}

	/********************************************************
	 ****************** ????????????????????????*****************************
	 *********************************************************/
	public void delete() {
		String[] ids = request.getParameterValues("ids");
		if (ids == null)
			return;
		String spliter = ",";
		String SQL = " where id in(" + join(spliter, ids) + ")";
		System.out.println("sql=" + SQL);
		dingdanSrv.delete(SQL);

	}

	public void save() {

		String shrtel = request.getParameter("shrtel");
		String shraddress = request.getParameter("shraddress");
		String errorurl = request.getParameter("errorurl");
		String shrname = request.getParameter("shrname");
		String des = request.getParameter("des");
		String xdren = request.getParameter("xdren");
		Shopcart shopcart = shopcartSrv.getCart("shopcart");
		HashMap<String, List<ShopcartItem>> groupCartItems = shopcart.getGroupCartItems();
		if (groupCartItems == null)
			return;
		for (String key : groupCartItems.keySet()) {
			Collection<ShopcartItem> list = groupCartItems.get(key);

			if (list == null) {
				request.setAttribute("errormsg", "????????????????????????");
				forward(errorurl);
				return;
			}

			Dingdan dingdan = new Dingdan();
			dingdan.setDdno(SequenceUtil.buildSequence("D"));
			Double totalprice = 0.0;
			for (ShopcartItem box : list) {

				Dingdanitems dd = new Dingdanitems();
				dd.setJiage(box.getPrice());
				dd.setJifen(0);
				dd.setShuliang(box.getCount());
				dd.setSpid(box.getId());
				dd.setSpname(box.getTitle());
				dd.setSpimage(box.getImgurl());
				dd.setDdno(dingdan.getDdno());
				totalprice += dd.getJiage() * dd.getShuliang();
				dditemsSrv.save(dd);
				Shangpin shangpin = shangpinSrv.load(" where id=" + dd.getSpid());
				if (shangpin.getKucun() >= dd.getShuliang()) {
					shangpin.setKucun(shangpin.getKucun() - dd.getShuliang());
					shangpinSrv.update(shangpin);
				}

			}

			Huiyuan huiyuan = huiyuanSrv.load("where accountname='" + xdren + "'");
			double discount = 1;
			if (huiyuan != null) {
				Hytype hytype = hytypeSrv.load("where id=" + huiyuan.getTypeid());
				if (hytype != null)
					discount = hytype.getDiscount();
			}
			dingdan.setXiadantime(new Date());
			dingdan.setXiadanren(xdren);

			dingdan.setTotalprice(totalprice * discount);
			dingdan.setShaccount(key);
			dingdan.setState(1);
			dingdan.setFahuotime(new Date());
			// ????????????
			dingdan.setShrtel(shrtel);
			dingdan.setShraddress(shraddress);
			dingdan.setShrname(shrname);
			dingdan.setDes(des);
			dingdanSrv.save(dingdan);
		}
		shopcartSrv.clear(cartName);
		request.getSession().removeAttribute("checkedProductList");
		request.getSession().removeAttribute("groupedProduct");
		String forwardurl = request.getParameter("forwardurl");

		forward(forwardurl);

	}

	// ????????????
	public void get() {
		String filter = "where 1=1 ";
		String xdren = request.getParameter("xiadanren");
		String shaccount = request.getParameter("shaccount");
		if (xdren != null) {
			filter += " and xiadanren='" + xdren + "'";
		}
		if (shaccount != null)
			filter += " and shaccount='" + shaccount + "'";

		int pageindex = 1;
		int pagesize = 10;
		// ??????????????????
		String currentpageindex = request.getParameter("currentpageindex");
		// ??????????????????
		String currentpagesize = request.getParameter("pagesize");
		// ???????????????
		if (currentpageindex != null)
			pageindex = new Integer(currentpageindex);
		// ?????????????????????
		if (currentpagesize != null)
			pagesize = new Integer(currentpagesize);
		List<Dingdan> listdingdan = dingdanSrv.getPageEntitys(filter, pageindex, pagesize);
		int recordscount = dingdanSrv.getRecordCount(filter == null ? "" : filter);
		request.setAttribute("listdingdan", listdingdan);
		PagerMetal pm = new PagerMetal(recordscount);
		// ????????????
		pm.setPagesize(pagesize);
		// ?????????????????????
		pm.setCurpageindex(pageindex);
		// ??????????????????
		request.setAttribute("pagermetal", pm);
		// ??????????????????
		dispatchParams(request, response);
		String forwardurl = request.getParameter("forwardurl");
		System.out.println("forwardurl=" + forwardurl);
		if (forwardurl == null) {
			forwardurl = "/admin/dingdanmanager.jsp";
		}
		forward(forwardurl);
	}

	@Autowired
	private DingdanService dingdanSrv = null;

	@Autowired
	private DingdanitemsService dditemsSrv = null;

	@Autowired
	private CommentService commentSrv = null;

	@Autowired
	private ShangpinService shangpinSrv = null;
	@Autowired
	private HuiyuanService huiyuanSrv = null;

	private String cartName = "shopcart";
	
	@Autowired
	private ShopcartService shopcartSrv = null;
	@Autowired
	private HytypeService hytypeSrv=null;

}
