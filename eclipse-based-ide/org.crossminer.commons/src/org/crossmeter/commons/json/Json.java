/*********************************************************************
* Copyright (c) 2017 FrontEndART Software Ltd.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
*
* Contributors:
*    Zsolt János Szamosvölgyi
*    Endre Tamás Váradi
*    Gergõ Balogh
**********************************************************************/
package org.crossmeter.commons.json;

import java.lang.reflect.Type;

import org.crossmeter.commons.context.sourcecode.lineinfo.detail.ASTDetail;
import org.crossmeter.commons.context.sourcecode.lineinfo.detail.ASTDetailKind;
import org.crossmeter.commons.json.deserializer.ASTDetailDeserializer;
import org.crossmeter.commons.json.deserializer.ASTDetailKindDeserializer;
import org.crossmeter.commons.json.deserializer.LibraryAPIElementDeserializer;
import org.crossmeter.commons.json.deserializer.RecommendationDeserializer;
import org.crossmeter.commons.json.deserializer.TransactionDeserializer;
import org.crossmeter.commons.json.serializer.ASTDetailKindSerializer;
import org.crossmeter.commons.libraryapi.LibraryAPIElement;
import org.crossmeter.commons.recommendation.Recommendation;
import org.crossmeter.commons.transaction.Transaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Provides a json serializer class with specific settings.
 *
 */
public class Json {
	
	/**
	 * Returns the serialized object's json code.
	 * @param the object to be serialized
	 * @return the {@link String} serialized json code
	 */
	public static String toJson(Object object) {
		return gsonSerializer().toJson(object);
	}
	
	private static Gson gsonSerializer() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		registerSerializers(gsonBuilder);
		
		return gsonBuilder.create();
	}
	
	/**
	 * Returns the serialized object's formatted json code.
	 * @param the object to be serialized
	 * @return the {@link String} serialized json code with formatting
	 */
	public static String toFormattedJson(Object object) {
		return gsonFormattedSerializer().toJson(object);
	}
	
	private static Gson gsonFormattedSerializer() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		registerSerializers(gsonBuilder);
		gsonBuilder.setPrettyPrinting();
		
		return gsonBuilder.create();
	}
	
	private static GsonBuilder registerSerializers(GsonBuilder gsonBuilder) {
		gsonBuilder.registerTypeAdapter(ASTDetailKind.class, new ASTDetailKindSerializer());
		
		return gsonBuilder;
	}
	
	/**
	 * Returns the deserialized object from the given json code.
	 * @param <T> the type of returned object
	 * @param json the {@link String} json code
	 * @param typeOf
	 * @return the deserialized object
	 */
	public static <T> T fromJson(String json, Type typeOf) {
		return gsonDeserializer().fromJson(json, typeOf);
	}
	
	private static Gson gsonDeserializer() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		registerDeserializers(gsonBuilder);
		
		return gsonBuilder.create();
	}
	
	private static GsonBuilder registerDeserializers(GsonBuilder gsonBuilder) {
		gsonBuilder.registerTypeAdapter(Transaction.class, new TransactionDeserializer());
		gsonBuilder.registerTypeAdapter(LibraryAPIElement.class, new LibraryAPIElementDeserializer());
		gsonBuilder.registerTypeAdapter(Recommendation.class, new RecommendationDeserializer());
		gsonBuilder.registerTypeAdapter(ASTDetail.class, new ASTDetailDeserializer());
		gsonBuilder.registerTypeAdapter(ASTDetailKind.class, new ASTDetailKindDeserializer());
		
		return gsonBuilder;
	}
	
}
