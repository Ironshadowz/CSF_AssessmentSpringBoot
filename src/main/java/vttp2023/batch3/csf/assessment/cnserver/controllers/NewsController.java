package vttp2023.batch3.csf.assessment.cnserver.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2023.batch3.csf.assessment.cnserver.models.News;
import vttp2023.batch3.csf.assessment.cnserver.models.TagCount;
import vttp2023.batch3.csf.assessment.cnserver.services.NewsService;

@RestController
public class NewsController 
{
	@Autowired
	private NewsService newsServ;


	@PostMapping(path="/uploadImg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE
			, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postUpload(@RequestBody String news ,@RequestPart MultipartFile myfile) throws IOException 
	{
		String id = newsServ.postNews(news, myfile);
		if(id!="Error")
		{
			JsonObject resp = Json.createObjectBuilder()
				.add("id", id)
				.build();
			return ResponseEntity.ok(resp.toString());
		}
		return ResponseEntity.badRequest().body(id);
	}

	@GetMapping(path="/getTags/{time}")
	public ResponseEntity<List<TagCount>> getTags(@PathVariable("time") String time )
	{
		List<TagCount> tags = newsServ.getTags(time);
		if(tags!=null)
		{			
			return ResponseEntity.ok(tags);
		}
		return ResponseEntity.notFound().build();
	}
	@GetMapping(path="/getNews/{news}/{time}")
	public ResponseEntity<List<News>> getNews(@PathVariable("news") String tag, @PathVariable("time") String time)
	{
		try
		{
			List<News> newsList = newsServ.getNewsByTag(tag, time);
			return ResponseEntity.ok(newsList);
		} catch(Exception ex)
		{
			return ResponseEntity.notFound().build();
		}
	}

	// TODO: Task 1


	// TODO: Task 2


	// TODO: Task 3

}
