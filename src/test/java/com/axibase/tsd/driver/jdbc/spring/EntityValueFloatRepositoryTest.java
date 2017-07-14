package com.axibase.tsd.driver.jdbc.spring;

import com.axibase.tsd.driver.jdbc.TestProperties;
import com.axibase.tsd.driver.jdbc.spring.entity.EntityValueFloat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AtsdRepositoryConfig.class })
public class EntityValueFloatRepositoryTest extends TestProperties {

	@Resource
	private EntityValueFloatRepository entityRepository;

	@Resource
	private DataSource dataSource;

	private JdbcOperations jdbc;

	@Before
	public void setUp() throws Exception {
		jdbc = new JdbcTemplate(dataSource);
	}

	@After
	public void tearDown() throws Exception {
		Connection connection = dataSource.getConnection();
		if (connection != null && !connection.isClosed())
			connection.close();
	}

	@Test
	public void testCount() {
		long count = entityRepository.count();
		assertTrue(TINY_TABLE_COUNT == -1 || count == TINY_TABLE_COUNT);
	}

	@Test
	public void testFindAll() {
		final PageRequest page = new PageRequest(0, 1000, Direction.DESC, "time", "value");
		final Page<EntityValueFloat> result = entityRepository.findAll(page);
		List<EntityValueFloat> list = result.getContent();
		final List<Map<String, Object>> map = jdbc.queryForList(
				String.format("SELECT entity, datetime, value FROM %s ORDER BY time, value DESC LIMIT 1000", TINY_TABLE));
		for (Map<String, Object> next : map) {
			final EntityValueFloat entityValueFloat = new EntityValueFloat(
					(String) next.get("entity"),
					(Timestamp) next.get("datetime"),
					(Float) next.get("value"));
			assertTrue(list.contains(entityValueFloat));
		}
	}

}