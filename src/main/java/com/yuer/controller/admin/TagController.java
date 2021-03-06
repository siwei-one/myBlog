package com.yuer.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yuer.entity.Page;
import com.yuer.entity.Tag;
import com.yuer.service.ITagService;

@Controller
@RequestMapping("/admin")
public class TagController {

	@Autowired
	private ITagService tagService;


	// page代表当前页数,当按上一页下一页会传page
	@GetMapping("/tags")
	public String tags(@RequestParam(value = "page", required = false) Integer page, Model model) {
		if (page == null || page == 0) {
			getPage(-1, false,model);
		} else {
			getPage(page, true,model);
			
		}

		return "admin/tags";
	}

	// 转到增加
	@GetMapping("/tags/input")
	public String input(Model model) {

		model.addAttribute("tag", new Tag());
		return "admin/tags-input";
	}

	// 转到编辑
	@GetMapping("/tags/{id}/input")
	public String update(@PathVariable Long id, Model model) {
		Tag tag = tagService.getTagById(id);

		model.addAttribute("tag", tag);
		return "admin/tags-input";
	}

	// 删除
	@GetMapping("/tags/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes attributes) {
		tagService.deleteTag(id);
		attributes.addFlashAttribute("message", "删除成功");
		return "redirect:/admin/tags";
	}

	// 增加操作
	@PostMapping("/tags")
	public String save(Tag tag, Model model, RedirectAttributes attributes) {
		// 这里还有一个疑问，数据库中id字段不是默认的，只存name会成功吗？ 好像会，，，有默认值？
		Tag t = tagService.getTagByTagName(tag.getTagName());
		if (t != null) {
			model.addAttribute("msg", "不能添加重复分类");
			return "admin/tags-input";
		} else {
			Integer num = tagService.saveTag(tag);
			if (num == null || num.intValue() <= 0) {
				attributes.addFlashAttribute("message", "新增失败");
			} else {
				attributes.addFlashAttribute("message", "新增成功");
			}

			return "redirect:/admin/tags";
		}

	}

	// 处理编辑
	@PostMapping("/tags/{id}")
	public String edit(@PathVariable Long id, Tag Tag, Model model, RedirectAttributes attributes) {
		Tag t = tagService.getTagByTagName(Tag.getTagName());
		if (t != null) {
			model.addAttribute("msg", "不能修改成已有分类");
			return "admin/tags-input";
		} else {
			Integer num = tagService.updateTag(id, Tag.getTagName());
			if (num == null || num.intValue() <= 0 ) {
				attributes.addFlashAttribute("message", "更新失败");
			} else {
				attributes.addFlashAttribute("message", "更新成功");
			}

			return "redirect:/admin/tags";
		}

	}

	public void getPage(Integer page, boolean flag, Model model) {
		// 先 new Page
		Page<Tag> page1 = new Page<Tag>();

		// 先查出数据条数，再计算得出多少页
		int total = tagService.getTotal();
		page1.countTotalPages(total);

		if (flag) {
			page1.setPage(page);
		}

		page1.setContent(tagService.listTagByParam(page1.getStart(), page1.getSize()));
		
		model.addAttribute("page", page1);

	}

}
