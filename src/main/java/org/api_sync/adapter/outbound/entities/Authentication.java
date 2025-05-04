package org.api_sync.adapter.outbound.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@AllArgsConstructor
@Entity
@Table(name = "authentication")
@NoArgsConstructor
@Builder
@Getter
public class Authentication {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private Long id;
	@Column(nullable = false)
	private String token;
	@Column(nullable = false)
	private String sign;
	@Column(nullable = false)
	private String expirationTime;
	@Column(nullable = false)
	private Integer puntoVenta;
	@Column(nullable = false)
	private Long empresaId;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean expired() {
		Date dateActual = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String aux =
				"" + expirationTime.substring(0, expirationTime.indexOf("T")) + " " + expirationTime.substring(expirationTime.indexOf("T") + 1, expirationTime.indexOf("T") + 9);
		Date expira = null;
		try {
			expira = hourdateFormat.parse(aux);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		if (dateActual.compareTo(expira) <= 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isValid() {
		return isNotBlank(token) && isNotBlank(sign);
	}
}
