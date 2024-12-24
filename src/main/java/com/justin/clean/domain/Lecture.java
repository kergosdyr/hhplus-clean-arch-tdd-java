package com.justin.clean.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
public class Lecture {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private Long presenterId;

    @Column
    private String title;

    @Column
    private LocalDate lectureDate;

    @ManyToOne
    @JoinColumn(
            name = "presenterId",
            referencedColumnName = "id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Presenter presenter;

    @Column
    private int attendeeCount;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void increaseAttendee() {
        this.attendeeCount++;
    }

    public boolean isAttendAvailable() {
        return this.attendeeCount < 30;
    }

    public boolean isLectureExpired(LocalDateTime registeredAt) {
        return this.lectureDate.isBefore(registeredAt.toLocalDate())
                || this.lectureDate.isEqual(registeredAt.toLocalDate());
    }

    public boolean isAttendNotAvailable() {
        return !isAttendAvailable();
    }
}
