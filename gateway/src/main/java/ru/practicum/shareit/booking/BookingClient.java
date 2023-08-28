package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    /*Добрый вечер, Вячеслав! Конкатенацию строк для создания пути убрал, а вот второй пункт я не совсем понял для чего
    * это делать, ведь нам был дан такой шаблон с ResponseEntity<Object>. Также вернул тестирование модуля server, а то
    * в прошлый раз я его забыл перенести :). Надеюсь на понимание, ведь времени у меня не так много((*/
    public ResponseEntity<Object> getBookings(Long userId, BookingState state, Integer from, Integer size) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/")
                .queryParam("state", state.name())
                .queryParam("from", from);

        if (size != null) {
            uriBuilder.queryParam("size", size);
        }

        return get(uriBuilder.build().toString(), userId, null);
    }

    public ResponseEntity<Object> getBookingsOwner(Long userId, BookingState state, Integer from, Integer size) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/owner")
                .queryParam("state", state.name())
                .queryParam("from", from);

        if (size != null) {
            uriBuilder.queryParam("size", size);
        }

        return get(uriBuilder.buildAndExpand().toString(), userId, null);
    }


    public ResponseEntity<Object> create(Long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> update(Long bookingId, Long userId, Boolean approved) {
        String path = "/" + bookingId + "?approved=" + approved;
        return patch(path, userId, null, null);
    }
}
