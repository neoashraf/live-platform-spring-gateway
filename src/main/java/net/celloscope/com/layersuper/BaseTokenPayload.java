package net.celloscope.com.layersuper;

import lombok.Data;
import net.celloscope.com.notification.Notification;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Data
public class BaseTokenPayload implements Notification {

    private List<Error> errors;

    @Override
    public void addError(String message, HttpStatus httpStatus) {
        if (errors == null)
            errors = new ArrayList<>();
        errors.add(new Error(message, httpStatus));
    }

    @Override
    public Boolean hasError() {
        return errors != null && !errors.isEmpty();
    }

    @Override
    public List<Error> getErrors() {
        return errors;
    }

    @Override
    public Error getFirstError() {
        if(hasError()) return errors.get(0);
        return null;
    }
}
