package ua.khnu.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ua.khnu.service.IsDayOffService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Collectors;

import static javax.ws.rs.HttpMethod.GET;
import static ua.khnu.util.Constants.TIME_ZONE_ID;

@Service
public class IsDayOffServiceImpl implements IsDayOffService {
    private static final Logger LOG = LogManager.getLogger(IsDayOffServiceImpl.class);
    private static final String URL_TEMPLATE = "https://isdayoff.ru/api/getdata?year=%s&month=%s&day=%s&cc=ua";
    private Boolean isDayOff;
    private LocalDate lastSuccessesApiCall;

    @Override
    public boolean isTodayDayOf() {
        return isDayOff != null && LocalDate.now(ZoneId.of(TIME_ZONE_ID)).equals(lastSuccessesApiCall) ? isDayOff : callApi();
    }

    private boolean callApi() {
        var localDate = LocalDate.now(ZoneId.of(TIME_ZONE_ID));
        HttpURLConnection connection = null;
        String result;
        try {
            URL url = new URL(String.format(URL_TEMPLATE, localDate.getYear(), localDate.getMonth().getValue(), localDate.getDayOfMonth()));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(GET);
            result = readData(connection);
        } catch (IOException e) {
            LOG.error("IsDayOff service throw error", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        lastSuccessesApiCall = localDate;
        isDayOff = "1".equals(result);
        return isDayOff;
    }

    private String readData(HttpURLConnection connection) throws IOException {
        try (var inputStream = connection.getInputStream()) {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining());
        }
    }
}
