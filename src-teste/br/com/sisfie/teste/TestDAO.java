package br.com.sisfie.teste;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import br.com.arquitetura.DAO.UniversalDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-teste.xml" })
public class TestDAO {


	public void test1() throws Exception {
		System.out.println(1);
	}
	

}
