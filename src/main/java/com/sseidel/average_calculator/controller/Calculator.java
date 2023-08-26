/*
 * Copyright 2023 Sebastian Seidel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.sseidel.average_calculator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.sseidel.average_calculator.model.Course;
import com.sseidel.average_calculator.model.IOCourse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Calculator {

	//Nur für den Import und Export
	private final String TITLE = "title";
	private final String WEIGHTING = "weighting";
	private final String GRADE = "grade";
	private final String USED_FOR_CALC = "usedForCalc";

	private ObservableList<Course> courses = FXCollections.observableArrayList();
	private int nextUsableIndex = 0;

	private ObservableList<IOCourse> createExport(){
		ObservableList<IOCourse> export = FXCollections.observableArrayList();
		for (Course c: this.courses) {
			if (c.getId() >= 0)
				export.add(c.getExportObj());
		}
		return export;
	}

	private boolean isEmptyNode(JsonNode Node){
		return (Node.get(TITLE) == null) && (Node.get(WEIGHTING) == null) && (Node.get(GRADE) == null)
				&& (Node.get(USED_FOR_CALC) == null);
	}

	private boolean validateNode(JsonNode node){
		return node.get(this.TITLE).getNodeType() == JsonNodeType.STRING &&
				node.get(this.GRADE).getNodeType() == JsonNodeType.NUMBER &&
				node.get(this.WEIGHTING).getNodeType() == JsonNodeType.NUMBER &&
				node.get(this.USED_FOR_CALC).getNodeType() == JsonNodeType.BOOLEAN;
	}

	private void readJson(String json) throws InvalidEntryException, InvalidJSONStructure {
		boolean showWarning = false;

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNodes = null;

		try {
			jsonNodes = objectMapper.readTree(json);
		} catch (JsonProcessingException e) {
			throw new InvalidJSONStructure("Structure of the loaded file is incorrect.");
		}

		for (JsonNode courseNode : jsonNodes) {
			if (this.isEmptyNode(courseNode)){
				showWarning = true;
				continue;
			}

			if (!this.validateNode(courseNode)){
				showWarning = true;
				continue;
			}

			String title = courseNode.get(this.TITLE).asText();
			int weighting = courseNode.get(this.WEIGHTING).asInt();
			double grade = courseNode.get(this.GRADE).asDouble();
			boolean usedForCalc = courseNode.get(this.USED_FOR_CALC).asBoolean();

			Course course = new Course(this.nextUsableIndex);
			course.setTitle(title);
			course.setWeighting(weighting);
			course.setGrade(grade);
			course.setUsedForCalc(usedForCalc);

			this.courses.add(course);
			this.nextUsableIndex += 1;
		}

		if (showWarning) {
			String msg = "Incorrect entries were not loaded.";
			throw new InvalidEntryException(msg);
		}
	}

	public int count() {
		return this.courses.size();
	}
	
	public void addCourse(String title, int weighting, double grade, boolean usedForCalc) {
		Course newCourse = new Course(nextUsableIndex);
		newCourse.setTitle(title);
		newCourse.setWeighting(weighting);
		newCourse.setGrade(grade);
		newCourse.setUsedForCalc(usedForCalc);
		this.courses.add(newCourse);
		this.nextUsableIndex += 1;
	}	
	
	public void addCourse(String title, int weighting, double grade) {
		this.addCourse(title, weighting, grade, true);
	}
	
	public void addCourse(Course newCourse) {
		Course copy = new Course(this.nextUsableIndex);
		copy.setTitle(newCourse.getTitle());
		copy.setWeighting(newCourse.getWeighting());
		copy.setGrade(newCourse.getGrade().get());
		copy.setUsedForCalc(newCourse.getUsedForCalc().get());
		this.courses.add(copy);
		this.nextUsableIndex += 1;
	}
	
	public void deleteCourse(int index) {
		Course temp = null;
		for (Course c : this.courses) {
			if (index == c.getId()) {
				temp = c;
				break;
			}
		}
		if (temp != null)
			this.courses.remove(temp);
	}
	
	public boolean contains(int index) {
		for (Course c : this.courses) {
			if (index == c.getId()) {
				return true;
			}
		}
		return false;
	}
	
	public Course getCourse(int index) {
		if (this.contains(index)) {
			return this.courses.get(index);
		}else {
			return null;
		}
	}
	
	public double calcAverage() {
		double sumWeightedGrade = 0;
		double sumWeighting = 0;
		
		for (Course c : this.courses) {
			int used = c.getUsedForCalc().get() ? 1 : 0;
			sumWeightedGrade += c.getGrade().get()*c.getWeighting()*used;
			sumWeighting += c.getWeighting()*used;
		}
		if (Math.round(sumWeightedGrade) == 0.0)
			return 0.00;
		else {
			//auf zwei Stellen nach dem Komma runden
			BigDecimal bd = new BigDecimal(sumWeightedGrade/sumWeighting).setScale(2, RoundingMode.HALF_UP);
			return bd.doubleValue();
		}
	}
	
	public String toString() {
		String result = "";
		for (Course c: this.courses) {
			result = result + c.toString() + "\n";
		}
		
		//letzten Zeilenumbruch entfernen
		if (!result.isEmpty())
			return result.substring(0, result.length() - 1);
		else
			return "";
	}
	
	public void loadFromJSON(String filename) throws FileNotFoundException, InvalidLoadFileFormat, InvalidJSONStructure, InvalidEntryException {
		this.nextUsableIndex = 0;
		this.courses.clear();

		// Prüfen, ob es die Datei gibt
		Path file = Paths.get(filename);
		if (!Files.exists(file)) {
			String msg = "File: \"" + filename + "\" not exist.";
			throw new FileNotFoundException(msg);
			//Erneut Laden bei Exception
		}

		//Fehler beim laden liefert InvalidLoadFileFormat
		String jsonContent = "";
		try{
			jsonContent = new String(Files.readAllBytes(file));
		}catch(IOException e){
			throw new InvalidLoadFileFormat("Incorrect JSON Format");
			//Erneut Laden bei Exception
		}

		//Unvollständige Einträge liefern InvalidEntryException
		this.readJson(jsonContent);
	}

	
	public boolean saveAsJson(String filename) {
		// Prüfen, ob schon Einträge erstellt wurden
		if (this.count() == 0)
			return true;

		// Liste mit IOCourses erzeugen
		ObservableList<IOCourse> exportList = this.createExport();

        // Jackson ObjectMapper zum Konvertieren von Daten in JSON verwenden
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter(); 
        try {
            // JSON-Datei erzeugen
			Path file = Paths.get(filename);
        	objectWriter.writeValue(file.toFile(), exportList);

            //System.out.println("Daten in JSON-Datei gespeichert.");
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Fehler beim Speichern der JSON-Datei.");
        }		
		return true;
	}

	public ObservableList<Course> getCourses(){
		return this.courses;
	}

	public double getUsedInPercent() {
		double result;
		if (this.courses.isEmpty()) {
			return 0.0;
		}

		int numNotUsed = 0;
		int numAll = 0;
		for ( Course c : this.courses) {
			int used = c.getUsedForCalc().get() ? 0 : 1;
			numNotUsed += c.getWeighting()*used;
			numAll += c.getWeighting();
		}

		if (numAll == 0)
			return 0.0;

		result = 100-((double) numNotUsed /numAll)*100;
		BigDecimal bd = new BigDecimal(result).setScale(1, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public int getSumWeighting(){
		int sum = 0;
		for (Course c : this.courses) {
			sum += c.getWeighting();
		}
		return sum;
	}
}
