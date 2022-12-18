package AwesomeCalendar.Filters.FilterObjects;

import AwesomeCalendar.Entities.User;
import com.google.gson.Gson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserFromHeaderConverter implements Converter<String, User> {
    @Override
    public User convert(String source) {
        Gson gson = new Gson();
        return gson.fromJson(source, User.class);
    }
}
