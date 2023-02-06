package ru.kakatya.dossier.dtos;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDto implements Serializable {
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String email;
    private String gender;
    private String maritalStatus;
    private int dependentAmount;
    private String passport;
    private String employment;
    private String account;

    @Override
    public String toString() {
        return "lastName=" + lastName + "\n" +
                "firstName=" + firstName + "\n" +
                "middleName=" + middleName + "\n" +
                "birthDate=" + birthDate + "\n" +
                "email=" + email + "\n" +
                "gender=" + gender + "\n" +
                "maritalStatus=" + maritalStatus + "\n" +
                "dependentAmount=" + dependentAmount + "\n" +
                "account=" + account + "\n" +
                "\npassport:\n" + passport + "\n" +
                "employment:\n" + employment + "\n";
    }
}
