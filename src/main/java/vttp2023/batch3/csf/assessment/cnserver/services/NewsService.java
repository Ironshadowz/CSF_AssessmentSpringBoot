package vttp2023.batch3.csf.assessment.cnserver.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2023.batch3.csf.assessment.cnserver.models.News;
import vttp2023.batch3.csf.assessment.cnserver.models.TagCount;
import vttp2023.batch3.csf.assessment.cnserver.repositories.ImageRepository;
import vttp2023.batch3.csf.assessment.cnserver.repositories.NewsRepository;

@Service
public class NewsService 
{	
	@Autowired
	private ImageRepository imgRepo;
	@Autowired
	private NewsRepository newsRepo;
	// TODO: Task 1
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns the news id
	public String postNews(String news, MultipartFile myfile) 
	{
		String resId = UUID.randomUUID().toString().substring(0, 8);
		long date = System.currentTimeMillis();
		JsonReader jsonReader = Json.createReader(new StringReader(news));
		JsonObject o = jsonReader.readObject();

		String id = imgRepo.saveImage(myfile);
		News newNews = new News();
		newNews.setId(resId);
		newNews.setPostDate(date);
		newNews.setTitle(o.getString("title"));
		newNews.setDescription(o.getString("description"));
		newNews.setImage(id);
		List<String> tags = new ArrayList<>();
		JsonArray jsonArray = o.getJsonArray("tagArray");
		if(jsonArray!=null)
		{
			for(int i=0; i<jsonArray.size(); i++)
			{
				tags.add(jsonArray.get(i).toString());
			}
		}
		newNews.setTags(tags);
		jsonReader.close();
		try
		{
			String result = newsRepo.insertForm(newNews);
			return result;
		} catch(Exception ex)
		{
			return "Error";
		}
	}
	 
	// TODO: Task 2
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns a list of tags and their associated count
	public List<TagCount> getTags(String count) 
	{
		List<TagCount> tags = newsRepo.getTags(Integer.valueOf(count));
		return tags;
	}

	// TODO: Task 3
	// Do not change the method name and the return type
	// You may add any number of parameters
	// Returns a list of news
	public List<News> getNewsByTag(String tag, String time) 
	{
		List<Document> news = newsRepo.getNews(tag, time);
		List<News> newsList = new ArrayList<>();
		for(Document d : news)
		{
			News newLis = new News();
			newLis.setDescription(d.getString("description"));
			newLis.setId(d.getString("_id"));
			newLis.setImage(d.getString("image"));
			newLis.setPostDate(d.getLong("postDate"));
			newLis.setTitle(d.getString("title"));
			newLis.setTags(d.getList("tags", String.class));
			newsList.add(newLis);
		}
		return newsList;
	}
	
}
