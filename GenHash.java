import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("HOD: " + encoder.encode("Hod@1234"));
        System.out.println("FAC: " + encoder.encode("Faculty@1234"));
        System.out.println("IQAC: " + encoder.encode("Iqac@1234"));
    }
}
