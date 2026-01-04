package ru.yandex.practicum.ewm.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Name must not be blank")
    @Size(min = 2, max = 250)
    private String name;

    @Email
    @NotBlank(message = "Email must not be blank")
    @Size(min = 6, max = 254)
    private String email;
}
