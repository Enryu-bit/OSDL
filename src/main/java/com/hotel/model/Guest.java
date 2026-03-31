package com.hotel.model;
import java.io.Serializable;
import java.time.LocalDate;
public class Guest implements Serializable {
    private static final long serialVersionUID = 5L;
    private String    guestId;
    private String    name;
    private String    phone;
    private String    email;
    private Integer   age;            
    private String    idProofType;    
    private String    idProofNumber;
    private Double    depositAmount;  
    private LocalDate registrationDate;
    public Guest(String guestId, String name, String phone, String email,
                 Integer age, String idProofType, String idProofNumber) {
        this.guestId          = guestId;
        this.name             = name;
        this.phone            = phone;
        this.email            = email;
        this.age              = age;            
        this.idProofType      = idProofType;
        this.idProofNumber    = idProofNumber;
        this.depositAmount    = 0.0;            
        this.registrationDate = LocalDate.now();
    }
    public String    getGuestId()                        { return guestId; }
    public void      setGuestId(String guestId)          { this.guestId = guestId; }
    public String    getName()                           { return name; }
    public void      setName(String name)                { this.name = name; }
    public String    getPhone()                          { return phone; }
    public void      setPhone(String phone)              { this.phone = phone; }
    public String    getEmail()                          { return email; }
    public void      setEmail(String email)              { this.email = email; }
    public Integer   getAge()                            { return age; }
    public void      setAge(Integer age)                 { this.age = age; }
    public String    getIdProofType()                    { return idProofType; }
    public void      setIdProofType(String idProofType)  { this.idProofType = idProofType; }
    public String    getIdProofNumber()                       { return idProofNumber; }
    public void      setIdProofNumber(String idProofNumber)   { this.idProofNumber = idProofNumber; }
    public Double    getDepositAmount()                       { return depositAmount; }
    public void      setDepositAmount(Double depositAmount)   { this.depositAmount = depositAmount; }
    public LocalDate getRegistrationDate()                          { return registrationDate; }
    public void      setRegistrationDate(LocalDate registrationDate){ this.registrationDate = registrationDate; }
    @Override
    public String toString() {
        return String.format("[%s] %s | Age: %d | Phone: %s | ID: %s - %s",
                guestId, name, age, phone, idProofType, idProofNumber);
    }
}