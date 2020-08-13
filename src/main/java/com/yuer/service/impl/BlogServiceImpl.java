package com.yuer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yuer.dao.IBlogDao;
import com.yuer.dao.ITagDao;
import com.yuer.dao.ITypeDao;
import com.yuer.dao.IUserDao;
import com.yuer.entity.Blog;
import com.yuer.entity.BlogQuery;
import com.yuer.entity.Tag;
import com.yuer.service.IBlogService;
import com.yuer.util.MarkdownUtils;

@Service
public class BlogServiceImpl implements IBlogService {

	@Autowired
	private IBlogDao blogDao;

	@Autowired
	private ITypeDao typeDao;

	@Autowired
	private ITagDao tagDao;

	@Autowired
	private IUserDao userDao;

	@Transactional
	@Override
	public Integer saveBlog(Blog blog) {
		if (blog.getId() == null) {
			blog.setCreateTime(new Date());
			blog.setUpdateTime(new Date());
			blog.setViewCounts(0);

			Integer num = blogDao.saveBlog(blog);
			// 这里保存进数据库后mybatis会返回id到实体类中，通过在insert标签中声明useGeneratedKeys="true"
			// keyProperty="id"做到此点
			Long id = blog.getId();
			// 再进行另一个表的操作
			String tagIds = blog.getTagIds();
			List<Tag> list = listTag(tagIds);

			for (Tag tag : list) {
				num = blogDao.saveTags(id, tag.getId());
			}

			return num;
		} else {
			blog.setUpdateTime(new Date());

			Integer num = updateBlog(blog);

			Long id = blog.getId();
			// 再进行另一个表的操作
			String tagIds = blog.getTagIds();
			List<Tag> list = listTag(tagIds);

			// 先删除所有之前的关于该博客的标签再插入
			num = blogDao.deleteTags(id);
			for (Tag tag : list) {
				num = blogDao.saveTags(id, tag.getId());

			}

			return num;
		}

	}

	public List<Tag> listTag(String tagIds) {
		List<Tag> list = new ArrayList<Tag>();
		if (tagIds != null && !"".equals(tagIds)) {
			String[] ids = tagIds.split(",");
			for (String id : ids) {
				list.add(tagDao.getTagByColumn("id",id));
			}

		}

		return list;
	}

	@Transactional
	@Override
	public Integer deleteBlog(Long id) {
		// 先删除所有之前的关于该博客的标签再删除博客
		int num = blogDao.deleteTags(id);
		
		num = blogDao.deleteBlog(id);

		return num;

	}

	@Transactional
	@Override
	public Integer updateBlog(Blog blog) {
		// 先查看此id是否存在
		// 不存在就直接返回null
		Blog t = blogDao.getBlogByColumn("id",blog.getId() + "");
		if (t == null) {
			return null;
		}

		return blogDao.updateBlog(blog);
	}

	@Transactional
	@Override
	public Blog getBlogById(Long id) {
		return blogDao.getBlogByColumn("id",id + "");
	}

	@Transactional
	@Override
	public List<Blog> listBlog() {
		return blogDao.listBlog();
	}

	@Transactional
	@Override
	public List<Blog> listBlogByParam(int start, int size) {
		List<Blog> list = blogDao.listBlogByParam(start, size);

		// 根据list中blog的typeId查出再放入type
		setValues(list,0);

		return list;
	}

	@Transactional
	@Override
	public List<Blog> listBlogByParamAndPublished(int start, int size) {
		List<Blog> list = blogDao.listBlogByParamAndPublished(start, size);

		// 根据list中blog的typeId查出再放入type
		// 还有User和Tag(这里暂时不需要显示tag)
		setValues(list,1);

		return list;
	}

	@Transactional
	@Override
	public List<Blog> listBlogTop(Integer size) {
		return blogDao.listBlogTop(size);
	}

	@Transactional
	@Override
	public Integer getTotal() {
		return blogDao.getTotal();
	}

	@Transactional
	@Override
	public Blog getBlogByBlogName(String blogName) {
		return blogDao.getBlogByColumn("blog_name",blogName);
	}

	@Transactional
	@Override
	public List<Blog> getBlogByParams(@Param("blog") BlogQuery blog, @Param("start") Integer start,
			@Param("size") Integer size) {
		List<Blog> list = blogDao.getBlogByParams(blog, start, size);

		// 根据list中blog的typeId查出再放入type
		setValues(list,0);

		return list;
	}

	@Transactional
	@Override
	public Integer getTotalByParams(BlogQuery blog) {
		return blogDao.getTotalByParams(blog);
	}

	@Transactional
	@Override
	public List<Long> getTags(Long blogId) {
		return blogDao.getTags(blogId);
	}

	@Transactional
	@Override
	public Integer getTotalAndPublished() {
		return blogDao.getTotalAndPublished();
	}

	@Transactional
	@Override
	public List<Blog> listBlogBySerach(String query, Integer start, Integer size) {
		List<Blog> list = blogDao.listBlogBySerach(query, start, size);

		// 根据list中blog的typeId查出再放入type
		setValues(list,1);
		return list;
	}

	
	
	@Transactional
	@Override
	public List<Blog> listBlogByTypeId(Long id, Integer start, Integer size) {
		List<Blog> list = blogDao.listBlogByTypeId(id, start, size);

		// 根据list中blog的typeId查出再放入type
		setValues(list,1);
		return list;
	}
	
	@Transactional
	@Override
	public List<Blog> listBlogByTagId(Long id, int start, int size) {
		List<Blog> list = blogDao.listBlogByTagId(id, start, size);

		// 这里应该就放tag和user?
		setValues(list,2);
		return list;
	}


	@Transactional
	@Override
	public Blog getAndConvert(Long id) {
		Blog blog = blogDao.getBlogByColumn("id",id + "");

		if (blog == null) {
			return null;
		}
		blog.setUser(userDao.findById(blog.getUser().getId()));
		
		Blog b = new Blog();
		BeanUtils.copyProperties(blog, b);
		String content = b.getContent();
		b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

		// 游览次数加一
		blogDao.updateViewCounts(id);
		return b;
	}

	
	@Transactional
	@Override
	public Integer getTotalAndPublishedAndTypeId(Long id) {
		return blogDao.getTotalAndPublishedAndTypeId(id);
	}

	

	@Transactional
	@Override
	public Integer getTotalAndPublishedAndTagId(Long id) {
		return blogDao.getTotalAndPublishedAndTagId(id);
	}

	@Transactional
	@Override
	public Integer getTotalAndPublishedAndSearch(String query) {
		return blogDao.getTotalAndPublishedAndSearch(query);
	}
	
	@Transactional
	@Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogDao.findGroupYear();
        // 先试下hashMap,不能保证按插入排序取出，使用LinkedHashMap 试下
//        Map<String, List<Blog>> map = new HashMap<>();
        Map<String, List<Blog>> map = new LinkedHashMap<>();
        for (String year : years) {
            map.put(year, blogDao.findByYear(year));
        }
        return map;
    }
	
	/**
	 * 将很多处一致的代码封装起来
	 * 给list中的blog赋值成员变量
	 * @param list
	 */
	private void setValues(List<Blog> list, int flag) {
		
		for (Blog b : list) {
			b.setType(typeDao.getTypeByColumn("id",b.getType().getId() + ""));
			if (flag == 1) {
				b.setUser(userDao.findById(b.getUser().getId()));				
			}
			
			if (flag == 2) {
				b.setUser(userDao.findById(b.getUser().getId()));				
				
				// 这里得先根据博客名找到对应得tags
				b.setTags(tagDao.listTagByBlogId(b.getId()));
			}
			
		}
		
		
	}

	
	
}
