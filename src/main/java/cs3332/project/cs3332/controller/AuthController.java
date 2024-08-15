package cs3332.project.cs3332.controller;

import cs3332.project.cs3332.model.Admin;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.model.Student;
import cs3332.project.cs3332.components.JwtTokenUtil;
import cs3332.project.cs3332.repository.UserRepository;
import cs3332.project.cs3332.service.SystemSettingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SystemSettingService systemSettingService;

    // Đăng nhập và nhận access token + refresh token
    @PostMapping("/login")
    public ResponseEntity<ResponseObject> createAuthenticationToken(@RequestBody AuthRequest authRequest)
            throws Exception {

        // Tiến hành xác thực thông tin đăng nhập
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Kiểm tra nếu người dùng không phải là admin và đăng nhập bị tắt
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !systemSettingService.isLoginAllowed()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseObject("error", "Login is currently disabled by the administrator.", null));
        }

        // Nếu mọi thứ hợp lệ, tạo token đăng nhập
        final String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(new ResponseObject("success", "Login successful", tokens));
    }

    // Chỉ admin mới có quyền tạo tài khoản sinh viên
    @PostMapping("/register/student")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> registerStudent(@RequestBody RegisterStudentRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ResponseObject("error", "Username is already taken!", null));
        }

        Student newStudent = new Student(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getName(),
                registerRequest.getProgram(),
                registerRequest.getMaxCredits());

        userRepository.save(newStudent);
        return ResponseEntity.ok(new ResponseObject("success", "Student registered successfully!", newStudent));
    }

    // Tạo tài khoản admin (Chỉ dành cho Admin)
    @PostMapping("/register/admin")
    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> registerAdmin(@RequestBody RegisterAdminRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ResponseObject("error", "Username is already taken!", null));
        }

        Admin newAdmin = new Admin(
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getDepartment());

        userRepository.save(newAdmin);
        return ResponseEntity.ok(new ResponseObject("success", "Admin registered successfully!", newAdmin));
    }

    // Refresh token
    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtTokenUtil.isTokenExpired(refreshToken)) {
            return ResponseEntity.status(403).body(new ResponseObject("error", "Refresh token is expired", null));
        }

        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtTokenUtil.generateAccessToken(userDetails);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", refreshToken);

        return ResponseEntity.ok(new ResponseObject("success", "Token refreshed successfully", tokens));
    }
}

class AuthRequest {
    private String username;
    private String password;

    // getters and setters
    String getUsername() {
        return username;
    }

    void setUsername(String username) {
        this.username = username;
    }

    String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }
}

class AuthResponse {
    private final String jwt;

    public AuthResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}

// Request class for registering a student
class RegisterStudentRequest {
    private String username;
    private String password;
    private String studentId;
    private String name;
    private String program;
    private int maxCredits;

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getMaxCredits() {
        return maxCredits;
    }

    public void setMaxCredits(int maxCredits) {
        this.maxCredits = maxCredits;
    }
}

// Request class for registering an admin
class RegisterAdminRequest {
    private String username;
    private String password;
    private String adminId;
    private String department;

    // getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

// Request class for refreshing token
class RefreshTokenRequest {
    private String refreshToken;

    // getters and setters
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
