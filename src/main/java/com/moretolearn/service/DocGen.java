package com.moretolearn.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class DocGen implements IdentifierGenerator{
	
	private static final long serialVersionUID = 1L;

	public int genId() {
		Random random = new Random();
		return random.nextInt(100);
	}

	public String genAnotherId() {

		return RandomStringUtils.randomNumeric(9);
	}

	@Override
	public Serializable generate(SharedSessionContractImplementor si, Object o) throws HibernateException {
		Calendar calendar = Calendar.getInstance();
		return "DOC" + this.genId() + this.genAnotherId()
				+ calendar.get(Calendar.DATE) + calendar.get(Calendar.YEAR);
	}

	

}