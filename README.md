# MedChain Registration Backend — OTP + Auto Username/Password

Ye backend tumhare `register.html` mein jo functions call ho rahe the
(`apiSendOtp`, `apiVerifyOtp`, `apiSendEmailOtp`, `apiVerifyEmailOtp`,
`apiRegisterWithPhoto`) unko actually implement karta hai.

## Flow (jo tumne describe kiya)
1. User mobile number bharta hai → **Send OTP** → SMS pe OTP jaata hai.
2. User email bharta hai → **Send OTP** → Email pe OTP jaata hai.
3. User dono OTP verify karta hai → tabhi **Submit** button enable hota hai
   (ye already frontend mein `refreshSubmitState()` se ho raha hai).
4. Submit dabaane par backend:
   - check karta hai ki mobile + email dono verified hain,
   - random **username + strong password** generate karta hai,
   - password ko **bcrypt** se hash karke store karta hai (plain text kabhi save nahi hota),
   - username/password ko SMS + email dono pe bhejta hai,
   - response mein credentials wapas nahi bheja jaata (security ke liye) — sirf
     "check your mobile/email" success message.
5. User apne mobile/email pe mile username+password se `login.html` pe login karta hai
   (`apiLogin` bhi diya hai as example, tumhare login.html ke hisaab se wire kar sakte ho).

## Setup

```bash
cd backend
npm install
cp .env.example .env
```

`.env` mein apne SMTP (Gmail app password ya koi bhi SMTP provider) aur
Twilio (ya jo bhi SMS gateway use karna ho — MSG91/Fast2SMS bhi easily swap ho
sakta hai `sendSms()` function mein) credentials daalo.

**Development shortcut:** agar `.env` mein SMTP_USER / TWILIO creds khaali
chhodo, to server actual SMS/email bhejने ki jagah console mein OTP/credentials
print kar dega — is se tum poora flow bina kisi paid service ke test kar sakte ho.

```bash
npm run dev   # ya: npm start
```

Server `http://localhost:5000` pe chalega.

## Frontend
`frontend/api-client.js` ko apne `register.html` ke saath use karo (already
same file name/functions match karte hain jo tumhare HTML mein call ho rahe
the). Bas `API_BASE` variable ko apne backend URL pe point karo.

## Important production notes
- In-memory `Map`/`Array` sirf demo/testing ke liye hai — real app mein
  MongoDB/PostgreSQL/MySQL use karo.
- OTP 5 min mein expire hota hai, 5 galat attempts ke baad naya OTP maangna
  padega — `.env` se ye values tune kar sakte ho.
- Password kabhi bhi plain text mein store/return nahi hota.
- Rate-limiting (e.g. `express-rate-limit`) add karna recommended hai taaki
  koi OTP endpoint spam na kare.
