package com.nttdata.BancaProductPassive.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = {"identityNumber"})
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "passiveProduct")
public class ProductPassive {
    @Id
    private String idProductPassive;

    private String account;

    private String identityNumber;

    @NotNull
    private String typeAccount;

    @NotNull
    private LocalDate dateRegister;

    @NotNull
    private Double availableAmount;

}
