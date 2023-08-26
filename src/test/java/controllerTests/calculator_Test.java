package controllerTests;

import static org.junit.jupiter.api.Assertions.*;

import com.sseidel.average_calculator.controller.InvalidEntryException;
import com.sseidel.average_calculator.controller.InvalidJSONStructure;
import org.junit.jupiter.api.*;
import java.io.File;

import com.sseidel.average_calculator.controller.Calculator;
import com.sseidel.average_calculator.model.Course;

class Calculator_Test {

	public static String loadPath = System.getProperty("user.dir") + "/src/test/resources/Test.json";
	public static String loadPath_EmptyFile = System.getProperty("user.dir") + "/src/test/resources/Test_EmptyFile.json";
	public static String loadPath_InvalidEntryKeys = System.getProperty("user.dir") + "/src/test/resources/Test_FileInvalidEntryKeys.json";
	public static String loadPath_InvalidStructure = System.getProperty("user.dir") + "/src/test/resources/Test_FileInvalidStructure.json";
	public static String savePath = System.getProperty("user.dir") + "/src/test/resources/SaveTest.json";

	
	@AfterEach
	void tearDown() {
		//alte datei entfernen
		File f = new File(savePath);	
		if (f.exists())
			f.delete();
	}
	
	@Test
	void testCount() {
		Calculator calc = new Calculator();
		assertEquals(0, calc.count());
		calc.addCourse("Mathe 1", 6, 2.0);
		assertEquals(1, calc.count());
	}
	
	@Test
	void testAddCourse() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 2.0);
		calc.addCourse("Informatik 1", 6, 2.0);
		assertEquals(3, calc.count());		
	}
	
	@Test
	void testAddCourseObj() {
		Calculator calc = new Calculator();
		Course c = new Course(1);
		c.setGrade(1.0);
		c.setTitle("Mathe 1");
		c.setUsedForCalc(false);
		c.setWeighting(6);	
		calc.addCourse(c);
		assertEquals(1, calc.count());		
	}
	
	@Test
	void testDeletCourse() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 2.0);
		calc.addCourse("Informatik 1", 6, 2.0);
		calc.deleteCourse(1);
		assertEquals(2, calc.count());
		assertTrue(calc.contains(0));
		assertFalse(calc.contains(1));
		assertTrue(calc.contains(2));		
	}
	
	@Test
	void testDeletCourseNotExist() {
		Calculator calc = new Calculator();
		calc.deleteCourse(1);
		assertEquals(0, calc.count());		
	}
	
	@Test
	void testContains() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		assertTrue(calc.contains(0));
		assertFalse(calc.contains(1));	
	}
	
	@Test
	void testCalcAverage() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 1.7);
		calc.addCourse("Informatik 1", 6, 1.3);
		assertEquals(1.67, calc.calcAverage());		
	}
	
	@Test
	void testCalcAverageSpecial() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 1.7, false);
		calc.addCourse("Informatik 1", 6, 1.3);
		assertEquals(1.65, calc.calcAverage());		
	}

	@Test
	void testCalcAverageFromNothing() {
		Calculator calc = new Calculator();
		assertEquals(0.00, calc.calcAverage());		
	}
	
	@Test
	void testToString() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 1.7, false);
		
		
		String excpted = "Name: Mathe 1, Grade: 2.0, Wieghting: 6\n"+
				         "Name: Physik 1, Grade: 1.7, Wieghting: 6";
		
		assertEquals(excpted, calc.toString());			
	}
	
	@Test
	void testGetCourse() {
		Calculator calc = new Calculator();
		calc.addCourse("Mathe 1", 6, 2.0);
		calc.addCourse("Physik 1", 6, 1.7, false);
		
		assertNotNull(calc.getCourse(0));
		assertNull(calc.getCourse(-1));
		
		Course c = calc.getCourse(0);
		assertEquals("Mathe 1", c.getTitle());	
	}
	
	@Test
	void testSaveAsJSON() {
		Calculator calc = new Calculator();
		try{
			calc.loadFromJSON(loadPath);
		}catch (Exception ignored){
		}
		calc.getCourse(0).setGrade(3.0);
		assertTrue(calc.saveAsJson(savePath));
		
		//Gespeicherte Bearbeitung überprüfen
		Calculator checkCalc = new Calculator();
		try{
			checkCalc.loadFromJSON(savePath);
		}catch (Exception ignored){
		}
		assertEquals(3.0, checkCalc.getCourse(0).getGrade().get());
	}
	 
	@Test
	void testLoadFromJSON() {
		Calculator calc = new Calculator();
		try{
			calc.loadFromJSON(loadPath);
		}catch (Exception ignored){
			fail("Exceptions are thrown");
		}
		assertEquals(3, calc.count());
		assertEquals("Mathe 1", calc.getCourse(0).getTitle());
		calc.addCourse("Bio 1", 6, 1.3, true);
		assertEquals(4, calc.count());
		assertNotNull(calc.getCourse(3));

	}

	@Test
	void testLoadFromEmptyJSON() {
		Calculator calc = new Calculator();
		try {
			calc.loadFromJSON(loadPath_EmptyFile);
			assertEquals(0, calc.count());
		} catch (Exception e){
			fail(e.getClass().toString());
		}
	}

	@Test
	void testLoadFromInvalidEntryKeys() {
		Calculator calc = new Calculator();
		try {
			assertThrows(InvalidEntryException.class, () -> {
				calc.loadFromJSON(loadPath_InvalidEntryKeys);
			});
			assertEquals(0, calc.count());
		} catch (Exception ignored){
		}
	}

	@Test
	void testLoadFromInvalidJSONStructure() {
		Calculator calc = new Calculator();
		try {
			assertThrows(InvalidJSONStructure.class, () -> {
				calc.loadFromJSON(loadPath_InvalidStructure);
			});
			assertEquals(0, calc.count());
		} catch (Exception ignored){
		}
	}

	@Test
	void testGetCourses(){
		Calculator calc = new Calculator();
		try {
			calc.loadFromJSON(loadPath);

		} catch (Exception e){
			fail("Some Exception has thrown -" + e.getClass());
		}
		assertEquals(3, calc.count());

		Calculator calcTest = new Calculator();
		assertEquals(0, calcTest.count());
		calcTest.getCourses().addAll(calc.getCourses());
		assertEquals(3, calcTest.count());
	}

	@Test
	void testUsedInPercent(){
		Calculator calc = new Calculator();
		calc.addCourse("A", 6, 2.0);
		calc.addCourse("B", 6, 1.7, false);
		calc.addCourse("C", 6, 1.7);
		calc.addCourse("D", 6, 1.7);

		assertEquals(75.0, calc.getUsedInPercent());
	}

	@Test
	void testUsedInPercentFail(){
		Calculator calc = new Calculator();
		calc.addCourse("A", 0, 2.0);
		calc.addCourse("B", 0, 1.7, false);
		calc.addCourse("C", 0, 1.7);
		calc.addCourse("D", 0, 1.7);

		assertEquals(0.0, calc.getUsedInPercent());
	}

	@Test
	void testGetSumWeighting(){
		Calculator calc = new Calculator();
		calc.addCourse("A", 6, 2.0);
		calc.addCourse("B", 12, 1.7, false);

		assertEquals(18, calc.getSumWeighting());
	}

	@Test
	void testGetSumWeightingWithEmpty(){
		Calculator calc = new Calculator();
		assertEquals(0, calc.getSumWeighting());
	}
}
