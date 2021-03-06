/*******************************************************************************
 * Copyright (c) 2014 OSSMETER Partners.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yannis Korkontzelos - Implementation.
 *******************************************************************************/
package org.ossmeter.metricprovider.trans.newsgroups.threads.model;

import com.googlecode.pongo.runtime.Pongo;
import com.googlecode.pongo.runtime.querying.NumericalQueryProducer;
import com.googlecode.pongo.runtime.querying.StringQueryProducer;


public class ArticleData extends Pongo {
	
	
	
	public ArticleData() { 
		super();
		NEWSGROUPNAME.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		ARTICLENUMBER.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		ARTICLEID.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		DATE.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		FROM.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		SUBJECT.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		CONTENTCLASS.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
		REFERENCES.setOwningType("org.ossmeter.metricprovider.trans.newsgroups.threads.model.ArticleData");
	}
	
	public static StringQueryProducer NEWSGROUPNAME = new StringQueryProducer("newsgroupName"); 
	public static NumericalQueryProducer ARTICLENUMBER = new NumericalQueryProducer("articleNumber");
	public static StringQueryProducer ARTICLEID = new StringQueryProducer("articleId"); 
	public static StringQueryProducer DATE = new StringQueryProducer("date"); 
	public static StringQueryProducer FROM = new StringQueryProducer("from"); 
	public static StringQueryProducer SUBJECT = new StringQueryProducer("subject"); 
	public static StringQueryProducer CONTENTCLASS = new StringQueryProducer("contentClass"); 
	public static StringQueryProducer REFERENCES = new StringQueryProducer("references"); 
	
	
	public String getNewsgroupName() {
		return parseString(dbObject.get("newsgroupName")+"", "");
	}
	
	public ArticleData setNewsgroupName(String newsgroupName) {
		dbObject.put("newsgroupName", newsgroupName);
		notifyChanged();
		return this;
	}
	public int getArticleNumber() {
		return parseInteger(dbObject.get("articleNumber")+"", 0);
	}
	
	public ArticleData setArticleNumber(int articleNumber) {
		dbObject.put("articleNumber", articleNumber);
		notifyChanged();
		return this;
	}
	public String getArticleId() {
		return parseString(dbObject.get("articleId")+"", "");
	}
	
	public ArticleData setArticleId(String articleId) {
		dbObject.put("articleId", articleId);
		notifyChanged();
		return this;
	}
	public String getDate() {
		return parseString(dbObject.get("date")+"", "");
	}
	
	public ArticleData setDate(String date) {
		dbObject.put("date", date);
		notifyChanged();
		return this;
	}
	public String getFrom() {
		return parseString(dbObject.get("from")+"", "");
	}
	
	public ArticleData setFrom(String from) {
		dbObject.put("from", from);
		notifyChanged();
		return this;
	}
	public String getSubject() {
		return parseString(dbObject.get("subject")+"", "");
	}
	
	public ArticleData setSubject(String subject) {
		dbObject.put("subject", subject);
		notifyChanged();
		return this;
	}
	public String getContentClass() {
		return parseString(dbObject.get("contentClass")+"", "");
	}
	
	public ArticleData setContentClass(String contentClass) {
		dbObject.put("contentClass", contentClass);
		notifyChanged();
		return this;
	}
	public String getReferences() {
		return parseString(dbObject.get("references")+"", "");
	}
	
	public ArticleData setReferences(String references) {
		dbObject.put("references", references);
		notifyChanged();
		return this;
	}
	
	
	
	
}