package es.codeurjc.test.complex;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ComplexTest {

	@Test
	public void givenZero_thenRealPartIsZero_And_ImagPartIsZero() {
		
		Complex zero = new Complex(0, 0);
		
		double r = zero.getRealPart();
		double i = zero.getImaginaryPart();
		
		assertEquals(0, r, 0.001);
		assertEquals(0, i, 0.001);
	}

	@Test
	public void givenZeroAndOne_whenZeroAddToOne_thenOneIsObtained() {

		//Given
		Complex one = new Complex(1, 1);
		Complex zero = new Complex(0, 0);
		
		//When
		Complex result = zero.add(one);
		
		//Then		
		assertEquals(new Complex(1, 1), result);
	}

	@Test
	public void givenZeroAndOne_whenOneAddZero_thenOneIsObtained() {

		//Given
		Complex one = new Complex(1, 1);
		Complex zero = new Complex(0, 0);
		
		//When
		Complex result = one.add(zero);
		
		//Then		
		assertEquals(new Complex(1, 1), result);
	}

}
