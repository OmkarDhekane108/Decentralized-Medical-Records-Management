private final OtpService otpService;

    public AuthController(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                           TokenService tokenService, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.otpService = otpService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpRequest req) {
        if (req.mobile == null || req.mobile.isBlank()) {
            return ResponseEntity.status(400).body(new ErrorResponse("Mobile number is required."));
        }
        String otp = otpService.generateOtp(req.mobile);
        return ResponseEntity.ok(new OtpDemoResponse("OTP sent (demo mode — check response/console).", otp));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req) {
        if (!otpService.verifyOtp(req.mobile, req.otp)) {
            return ResponseEntity.status(400).body(new ErrorResponse("Invalid or expired OTP."));
        }
        return ResponseEntity.ok().build();
    }