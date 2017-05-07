package controllers;

import java.util.List;

import models.ArticleGroup;
import models.GSTType;
import models.Item;
import play.mvc.Controller;

public class ArticleGroups extends Controller{
	
	public static void show(String mode) {
		List<ArticleGroup> years = ArticleGroup.findAll();
		List<GSTType> companies = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(years, companies, mode);
	}
	
	public static void create(ArticleGroup artuckegroup) {		
		artuckegroup.save();
		show("add");
	}

	public static void edit(ArticleGroup artuckegroup) {
		artuckegroup.save();
		show("edit");
	}
	
	public static void filter(ArticleGroup articlegroup) {
		List<ArticleGroup> articlegroups = ArticleGroup.find("name = ?",
				articlegroup.name).fetch();
		renderTemplate("BusinessYears/show.html", "edit", articlegroups);
	}

	public static void delete(Long id) {
		if (id != null) {
			ArticleGroup articlegroup = ArticleGroup.findById(id);
			articlegroup.delete();
		}
		show("edit");
	}
	
	public static void business_partner(Long articlegroup_id) {
		if (articlegroup_id != null) {
			List<Item> items = Item.find("byArticleGroup_id", articlegroup_id).fetch();
			renderTemplate("Items/show.html", "edit", items, articlegroup_id);
		}
		show("edit");
	}

}
