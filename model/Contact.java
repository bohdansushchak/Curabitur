package sushchak.bohdan.curabitur.model;


public class Contact {
    public String contactId;
    public String name;
    public String avatar;
    public String email;

    @Override
    public String toString() {
        return "Contact{" +
                "contactId='" + contactId + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
