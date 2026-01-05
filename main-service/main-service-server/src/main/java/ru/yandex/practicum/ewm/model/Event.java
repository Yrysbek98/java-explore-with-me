package ru.yandex.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import ru.yandex.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "description", nullable = false, length = 7000)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid = false;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit = 0;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration = true;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "confirmed_requests")
    private Long confirmedRequests = 0L;

    @Column(name = "views")
    private Long views = 0L;

    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private EventState state = EventState.PENDING;

    public Event(String annotation, Category category, LocalDateTime eventDate, User user, Location location, Boolean paid, Integer participantLimit, Boolean requestModeration, String title) {
    }
}
