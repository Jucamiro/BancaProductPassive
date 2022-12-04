package com.nttdata.BancaProductPassive.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPassiveModel {

    private String idProductPassive;

    private String account;

    private String identityNumber;

    private String typeAccount;

    private LocalDate dateRegister;

    private Double availableAmount;
}
