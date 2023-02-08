package ru.kakatya.dossier.dtos;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreditDTO implements Serializable {
    private BigDecimal amount;
    private Integer term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private List<PaymentScheduleElementDto> paymentSchedule;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    @Override
    public String toString() {
        return "amount=" + amount + "\n" +
                "term=" + term + "\n" +
                "monthlyPayment=" + monthlyPayment + "\n" +
                "rate=" + rate + "\n" +
                "psk=" + psk + "\n" +
                "isInsuranceClient=" + isInsuranceEnabled + "\n" +
                "isSalaryClient=" + isSalaryClient + "\n";
    }
}
