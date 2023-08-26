package modelTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.sseidel.average_calculator.model.Course;

class Model_test {

	@Test
	void testCourse() {
		Course course = new Course(1);
		course.setTitle("Mathe 1");
		course.setGrade(2.0);
		course.setWeighting(6);
		course.setUsedForCalc(false);
		
		assertEquals(1, course.getId());
		assertEquals("Mathe 1", course.getTitle());
		assertEquals(2.0, course.getGrade().get());
		assertEquals(6, course.getWeighting());
        assertFalse(course.getUsedForCalc().get());
	}

	@Test
	void testInvalidCourse() {
		Course course = new Course(-1);
		course.setTitle("Mathe 1");
		course.setGrade(-2.0);
		course.setWeighting(-6);
		
		assertEquals(-1, course.getId());
		assertEquals("Mathe 1", course.getTitle());
		assertEquals(1.0, course.getGrade().get());
		assertEquals(1, course.getWeighting());
		assertTrue(course.getUsedForCalc().get());
	}
	
	@Test
	void testToStringCourse() {
		Course course = new Course(1);
		course.setTitle("Mathe 1");
		course.setGrade(2.0);
		course.setWeighting(6);
		
		assertEquals("Name: Mathe 1, Grade: 2.0, Wieghting: 6", course.toString());
	}
}
