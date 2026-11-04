package com.jebus.webclient;

import java.util.stream.Stream;

import jakarta.persistence.Converter;
import jakarta.persistence.AttributeConverter;

import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Entry;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.model.Authority;

public abstract class JpaConverters {

	@Converter(autoApply = true)
	public static class AccountTypeJPAConverter implements AttributeConverter<Account.AccountType, String> {

		@Override
		public String convertToDatabaseColumn(Account.AccountType type) {
			return type.getValue();
		}

		@Override
		public Account.AccountType convertToEntityAttribute(String dbData) {
			return Stream.of(Account.AccountType.values()).filter(a ->  a.getValue().contentEquals(dbData)).findFirst().orElse(null);
		}    
	}
	
	@Converter(autoApply = true)
	public static class EntryTypeJPAConverter implements AttributeConverter<Entry.EntryType, String> {

		@Override
		public String convertToDatabaseColumn(Entry.EntryType attribute) {
			return attribute.getValue();
		}

		@Override
		public Entry.EntryType convertToEntityAttribute(String dbData) {
			return Stream.<Entry.EntryType>of(Entry.EntryType.values()).filter(e -> e.getValue().equals(dbData)).findFirst().orElse(null);
		}
	}
	
	@Converter(autoApply = true)
	public static class JournalTypeJPAConverter implements AttributeConverter<Journal.JournalType, String> {

	    @Override
	    public String convertToDatabaseColumn(Journal.JournalType attribute) {
	        return attribute.getValue();
	    }

	    @Override
	    public Journal.JournalType convertToEntityAttribute(String dbData) {
	        return Stream.of(Journal.JournalType.values()).filter(j -> j.getValue().equals(dbData)).findFirst().orElse(null);
	    }
	}

	@Converter(autoApply = true)
	public static class AuthorityTypeConverter implements AttributeConverter<Authority.AuthorityType, String> {

		@Override
		public String convertToDatabaseColumn(Authority.AuthorityType attribute) {
			return attribute.getValue();
		}

		@Override
		public Authority.AuthorityType convertToEntityAttribute(String dbData) {
			return Stream.of(Authority.AuthorityType.values()).filter(type -> type.getValue().contentEquals(dbData)).findFirst().orElse(null);
		}
	}
}
