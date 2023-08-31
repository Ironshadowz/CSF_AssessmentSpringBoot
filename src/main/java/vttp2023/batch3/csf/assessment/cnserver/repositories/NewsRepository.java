package vttp2023.batch3.csf.assessment.cnserver.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import vttp2023.batch3.csf.assessment.cnserver.models.News;
import vttp2023.batch3.csf.assessment.cnserver.models.TagCount;

@Repository
public class NewsRepository 
{
	private static final String C_LISTING = "news";
	@Autowired
	MongoTemplate template;

	/*db.collection('news').insertOne
	({
		_id: news.getId(),
		postDate: news.getPostDate(),
		title: news.getTitle(),
		description: news.getDescription(),
		image: news.getImage(),
		tags: news.getTags()
	})
	*/
	public String insertForm(News news)
	{
		Document document = new Document();
		document.append("_id", news.getId());
		document.append("postDate", news.getPostDate());
		document.append("title", news.getTitle());
		document.append("description", news.getDescription());
		document.append("image", news.getImage());
		document.append("tags", news.getTags());
		//Inserting the document into the collection
		template.insert(document, C_LISTING);
		return null;
	}
	// TODO: Task 1 
	// Write the native Mongo query in the comment above the method
	/* 
	db.news.aggregate
	( [ 
		{$unwind : "$tags"} ,
		$group: 
		{
         tag : "$tags",
         count : {$count:{}}
		},
		sort({"count" : -1}),
		{$limit : 10}
	] )
	*/
	public List<TagCount> getTags(int count)
	{
		AggregationOperation unwindTags = Aggregation.unwind("tags");
		GroupOperation groupByTags=Aggregation.group("tags")
									.push("tags").as("tag")
									.count().as("count");
		ProjectionOperation headers = Aggregation.project("tag", "count");
		SortOperation sortByCount = Aggregation.sort(Sort.by(Direction.DESC, "count"));
		Aggregation pipeline = Aggregation.newAggregation(unwindTags,groupByTags,headers,sortByCount);
		AggregationResults<Document> results = template.aggregate(pipeline, C_LISTING, Document.class);
		List<Document> doc = results.getMappedResults();
		List<TagCount> tagList = new ArrayList<>();
		for(Document d: doc)
		{	
			TagCount tag = new TagCount(d.getString("tag"), d.getInteger("count"));
			tagList.add(tag);
		}
		return tagList;
	}
	// TODO: Task 2 
	// Write the native Mongo query in the comment above the method

	/*
	 * db.news.find
		( [ 
			$and: [{"tags": "news"}, {"time":{$lt:time}}]
		] )
	 */

	public List<Document> getNews(String news, String time)
	{	
		Query query = Query.query
			(
				Criteria.where("tags").is(news)
				.and("postDate").lte(time)
			).with(Sort.by(Sort.Direction.ASC, "postDate"));

		List<Document> results = template.find(query, Document.class, C_LISTING);	
		System.out.println("In repo "+results);
		return results;
	}

	// TODO: Task 3
	// Write the native Mongo query in the comment above the method


}
