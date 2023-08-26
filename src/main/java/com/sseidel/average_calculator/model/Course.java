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

package com.sseidel.average_calculator.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class Course {

	private int id;
	private String title;
	private int weighting;
	private final SimpleDoubleProperty grade = new SimpleDoubleProperty(1.0);
	private final SimpleBooleanProperty usedForCalc = new SimpleBooleanProperty(true);


	public Course(int id) {
		this.id = id;
		this.setTitle("");
		this.weighting = 1;
	}

	public int getId(){
		return this.id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getWeighting() {
		return weighting;
	}

	public void setWeighting(int weighting) {
		if (weighting >= 0)
			this.weighting = weighting;
	}

	public DoubleProperty getGrade() {
		//auf zwei Stellen nach dem Komma runden
		BigDecimal bd = new BigDecimal(this.grade.get()).setScale(1, RoundingMode.HALF_UP);
		return new SimpleDoubleProperty(bd.doubleValue());
	}

	public void setGrade(double grade) {
		if (grade > 0.0)
			this.grade.set(grade);
	}
	
	public void setUsedForCalc(boolean value) {
		this.usedForCalc.set(value);
	}

	public SimpleBooleanProperty getUsedForCalc() {
		return this.usedForCalc;
	}

	public String toString() {
		return String.format(Locale.US, "Name: %s, Grade: %.1f, Wieghting: %d", this.title, this.grade.get(), this.weighting);
	}
	public IOCourse getExportObj(){
		IOCourse exportObj = new IOCourse();
		exportObj.setTitle(this.title);
		exportObj.setGrade(this.grade.get());
		exportObj.setWeighting(this.weighting);
		exportObj.setUsedForCalc(this.usedForCalc.get());

		return exportObj;
	}

}
