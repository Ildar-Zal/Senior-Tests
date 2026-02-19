function generateComplexPassword() {
    const digits = "0123456789";
    const lower = "abcdefghijklmnopqrstuvwxyz";
    const upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    const spec = "!@#$%^&*";

    let pass = digits[Math.floor(Math.random() * digits.length)] +
               lower[Math.floor(Math.random() * lower.length)] +
               upper[Math.floor(Math.random() * upper.length)] +
               spec[Math.floor(Math.random() * spec.length)] +
               Math.random().toString(36).slice(-4); // Добиваем длину

    return pass;
}