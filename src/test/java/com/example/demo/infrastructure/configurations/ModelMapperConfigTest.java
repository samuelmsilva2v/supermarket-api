package com.example.demo.infrastructure.configurations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.modelmapper.convention.MatchingStrategies;

class ModelMapperConfigTest {

	@Test
	void getModelMapper_deveRetornarModelMapperComEstrategiaEstrita() {

		var modelMapper = new ModelMapperConfig().getModelMapper();

		assertNotNull(modelMapper);
		assertEquals(MatchingStrategies.STRICT, modelMapper.getConfiguration().getMatchingStrategy());
	}
}
