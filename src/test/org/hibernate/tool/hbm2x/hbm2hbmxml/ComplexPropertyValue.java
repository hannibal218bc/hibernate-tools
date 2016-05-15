package org.hibernate.tool.hbm2x.hbm2hbmxml;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * todo: describe ${NAME}
 *
 * @author Steve Ebersole
 */
public class ComplexPropertyValue implements PropertyValue {
	private Long id;
	private Map<Object, Object> subProperties = new HashMap<Object, Object>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Map<Object, Object> getSubProperties() {
		return subProperties;
	}

	public void setSubProperties(Map<Object, Object> subProperties) {
		this.subProperties = subProperties;
	}

	public String asString() {
		return "complex[" + keyString() + "]";
	}

	private String keyString() {
		StringBuffer buff = new StringBuffer();
		Iterator<Object> itr = subProperties.keySet().iterator();
		while ( itr.hasNext() ) {
			buff.append( itr.next() );
			if ( itr.hasNext() ) {
				buff.append( ", " );
			}
		}
		return buff.toString();
	}
}
