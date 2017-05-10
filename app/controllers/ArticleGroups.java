package controllers;

import java.util.List;

import models.ArticleGroup;
import models.GSTType;
import models.Item;
import play.mvc.Controller;

public class ArticleGroups extends Controller{
	
	public static void show(String mode) {
		List<ArticleGroup> articlegroups = ArticleGroup.findAll();
		List<GSTType> gsttypes = GSTType.findAll();
		if (mode == null || mode.equals(""))
			mode = "edit";
		render(articlegroups, gsttypes, mode);
	}
	
	public static void create(ArticleGroup articlegroup) {		
		articlegroup.save();
		show("add");
	}

	public static void edit(ArticleGroup articlegroup) {
		articlegroup.save();
		show("edit");
	}
	
	public static void filter(ArticleGroup articlegroup) {
		List<ArticleGroup> articlegroups = ArticleGroup.find("name = ?",
				articlegroup.name).fetch();
		renderTemplate("ArticleGroups/show.html", "edit", articlegroups);
	}

	public static void delete(Long id) {
		if (id != null) {
			ArticleGroup articlegroup = ArticleGroup.findById(id);
			articlegroup.delete();
		}
		show("edit");
	}
	
	public static void item(Long articlegroup_id) {
		if (articlegroup_id != null) {
			List<Item> items = Item.find("byArticleGroup_id", articlegroup_id).fetch();
			renderTemplate("Items/show.html", "edit", items, articlegroup_id);
		}
		show("edit");
	}

}