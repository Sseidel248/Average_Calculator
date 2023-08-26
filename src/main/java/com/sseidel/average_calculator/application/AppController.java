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

package com.sseidel.average_calculator.application;

import com.sseidel.average_calculator.controller.Calculator;
import com.sseidel.average_calculator.controller.InvalidEntryException;
import com.sseidel.average_calculator.controller.InvalidJSONStructure;
import com.sseidel.average_calculator.controller.InvalidLoadFileFormat;
import com.sseidel.average_calculator.model.Course;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.FileNotFoundException;

public class AppController {
	private final Calculator tableCalculator = new Calculator();
	@FXML
	private TableView<Course> dataTable;
	@FXML
	private TableColumn<Course, Integer> idCol;
	@FXML
	private TableColumn<Course, String> titleCol;
	@FXML
	private TableColumn<Course, Number> gradeCol;
	@FXML
	private TableColumn<Course, Integer> wightingCol;
	@FXML
	private TableColumn<Course, Boolean> usedCol;
	@FXML
	private Text averageText;
	@FXML
	private Text etcsText;

	private Stage primaryStage;
	private ContextMenu usedColContextMenu;

	private int lastIndex = 0;

	public void initialize() {
		this.dataTable.setEditable(true);

		this.setIdCol();
		this.setTitleCol();
		this.setGradeCol();
		this.setWightingCol();
		this.setUsedCol();

		this.dataTable.setItems(this.tableCalculator.getCourses());
	}

	public void initStage(Stage primaryStage){
		this.primaryStage = primaryStage;
	}

	@FXML
	public void onAddRow() {
		Course newCourse = new Course(lastIndex);
		this.tableCalculator.addCourse(newCourse);
		this.lastIndex += 1;
		this.dataTable.setItems(this.tableCalculator.getCourses());
		this.updateLabels();
	}

	@FXML
	public void onDeleteRow() {
		Course course = this.dataTable.getSelectionModel().getSelectedItem();
		if (course == null)
			return;
		this.tableCalculator.deleteCourse(course.getId());
		this.updateLabels();
	}

	@FXML
	public void onSaveBtnAction(){
		File saveFile = this.chooseSaveFile();
		if (saveFile != null) {
			this.tableCalculator.saveAsJson(saveFile.toString());
		}
	}

	@FXML
	public void onLoadBtnAction(){
		boolean canLoad = true;
		File loadFile = this.chooseLoadFile();
		try{
			this.tableCalculator.loadFromJSON(loadFile.toString());
		}catch(FileNotFoundException | InvalidLoadFileFormat | InvalidJSONStructure | InvalidEntryException e){
			if (e instanceof FileNotFoundException){
				if (MsgDialog.showInvalidFileMsg() == MsgDialog.MR_YES)
					this.onLoadBtnAction();
			}else if ((e instanceof InvalidLoadFileFormat) | (e instanceof InvalidJSONStructure)){
				MsgDialog.showWrongFileFormat();
				if (MsgDialog.showLoadAnotherFile() == MsgDialog.MR_YES)
					this.onLoadBtnAction();
				else
					canLoad = false;

			}else{
				MsgDialog.showIncorrectFileContent();
			}
		}

		if (canLoad)
			this.updateLabels();
	}

	@FXML
	public void onCommitTitle(TableColumn.CellEditEvent<Course, String> event) {
		Course course = event.getRowValue();
		course.setTitle(event.getNewValue());
	}
	@FXML
	public void onCommitGrade(TableColumn.CellEditEvent<Course, Number> event) {
		Course course = event.getRowValue();
		course.setGrade(event.getNewValue().doubleValue());
		this.setAverageLabel();
	}
	@FXML
	public void onCommitWighting(TableColumn.CellEditEvent<Course, Integer> event) {
		Course course = event.getRowValue();
		course.setWeighting(event.getNewValue());
		this.setNumofETCS();
	}
	@FXML
	public void onCommitUsedForCalc(TableColumn.CellEditEvent<Course, Boolean> event) {
		Course course = event.getRowValue();
		course.setUsedForCalc(event.getNewValue());
		this.setAverageLabel();
	}

	private void updateLabels(){
		this.setAverageLabel();
		this.setNumofETCS();
	}

	private void setAverageLabel(){
		String newText;
		if (Math.round(this.tableCalculator.getUsedInPercent()) > 0) {
			newText = "Used For Calc " + this.tableCalculator.getUsedInPercent() + "%";
			this.usedCol.setPrefWidth(130);
		}else{
			newText = "Used For Calc";
			this.usedCol.setPrefWidth(100);
		}
		this.usedCol.setText(newText);
		this.averageText.setText(String.valueOf(this.tableCalculator.calcAverage()));
	}



	private void setNumofETCS(){
		this.etcsText.setText(String.valueOf(this.tableCalculator.getSumWeighting()));
	}

	private void setAllUsedCells(boolean isUsed){
		for (Course c: this.tableCalculator.getCourses()) {
			c.setUsedForCalc(isUsed);
		}
		this.setAverageLabel();
	}

	private void decideContextMenuItemsEnabled(){
		usedColContextMenu.getItems().get(0).setDisable(this.dataTable.getItems().isEmpty());
		usedColContextMenu.getItems().get(1).setDisable(this.dataTable.getItems().isEmpty());
	}

	private void setUsedColContextMenu(){
		usedColContextMenu = new ContextMenu();
		MenuItem item1 = new MenuItem("Set all to \"Used\"");
		item1.setOnAction(event -> this.setAllUsedCells(true));
		MenuItem item2 = new MenuItem("Set all to \"Unused\"");
		item2.setOnAction(event -> this.setAllUsedCells(false));
		usedColContextMenu.getItems().addAll(item1, item2);
		usedColContextMenu.setOnShowing(event -> this.decideContextMenuItemsEnabled());
		usedCol.setContextMenu(usedColContextMenu);
	}

	private void setIdCol(){
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		idCol.setStyle("-fx-alignment: CENTER;");
	}

	private void setTitleCol(){
		titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	private void setGradeCol(){
		gradeCol.setCellValueFactory(cellData -> cellData.getValue().getGrade());
		gradeCol.setCellFactory(column -> {
			ComboBoxTableCell<Course, Number> cell = new ComboBoxTableCell<>(FXCollections.observableArrayList(
					1.0, 1.3, 1.7, 2.0, 2.3, 2.7, 3.0, 3.3, 3.7, 4.0, 5.0
			));
			cell.setConverter(new StringConverter<>() {
				@Override
				public String toString(Number value) {
					return value != null ? String.valueOf(value) : "";
				}

				@Override
				public Double fromString(String value) {
					try {
						return Double.parseDouble(value);
					} catch (NumberFormatException e) {
						return null;
					}
				}
			});
			return cell;
		});
		gradeCol.setStyle("-fx-alignment: CENTER;");
	}

	private void setWightingCol(){
		wightingCol.setCellValueFactory(new PropertyValueFactory<>("weighting"));
		wightingCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
		wightingCol.setStyle("-fx-alignment: CENTER;");
	}

	private void setUsedCol(){
		this.setUsedColContextMenu();
		usedCol.setCellValueFactory(cellData -> cellData.getValue().getUsedForCalc());
		usedCol.setCellFactory(column -> {
			ComboBoxTableCell<Course, Boolean> cell = new ComboBoxTableCell<>(FXCollections.observableArrayList(true, false));
			cell.setConverter(new StringConverter<>() {
				@Override
				public String toString(Boolean value) {
					return value ? "Used" : "Unused";
				}

				@Override
				public Boolean fromString(String value) {
					return "Used".equals(value);
				}
			});
			return cell;
		});
		usedCol.setStyle("-fx-alignment: CENTER;");
	}

	private FileChooser initFileChooser(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json"));
		return fileChooser;
	}

	private File chooseSaveFile(){
		FileChooser fileChooser = this.initFileChooser();
		//Prüft auch, ob eine Datei im Wunschordner gespeichert werden darf
        return fileChooser.showSaveDialog(primaryStage);
	}

	private File chooseLoadFile(){
		FileChooser fileChooser = this.initFileChooser();
		//Prüft auch, ob eine Datei valid ist
		return fileChooser.showOpenDialog(primaryStage);
	}

}
