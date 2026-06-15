using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using Microsoft.EntityFrameworkCore; // Adicionado para operações de banco assíncronas
using AuthServer.Models;
using AuthServer.Data; // Importa o seu AppDbContext
using AuthServer.Services;

namespace AuthServer.Controllers;

[ApiController]
[Route("v1")]
public class AuthController : ControllerBase
{
    private readonly AppDbContext _context; // Injeta o contexto do banco de dados diretamente
    private readonly TokenService _tokenService;

    public AuthController(AppDbContext context, TokenService tokenService)
    {
        _context = context;
        _tokenService = tokenService;
    }

    // =========================
    // LOGIN
    // =========================
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginRequest model)
    {
        // Busca o usuário diretamente no banco de dados usando o DbContext
        var user = await _context.Users.FirstOrDefaultAsync(u => u.Email == model.Email);

        if (user == null || !BCrypt.Net.BCrypt.Verify(model.Senha, user.Senha))
            return Unauthorized("Usuário ou senha inválidos");

        var token = _tokenService.GenerateToken(user);

        return Ok(new
        {
            user = user.Email,
            role = user.Papel,
            token = token
        });
    }

    // =========================
    // REGISTER
    // =========================
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterRequest model)
    {
        // Verifica diretamente no banco se o e-mail já existe
        var existingUser = await _context.Users.FirstOrDefaultAsync(u => u.Email == model.Email);

        if (existingUser != null)
            return BadRequest("Usuário já existe");

        var user = new User
        {   
            Nome = model.Name,
            Email = model.Email,
            Senha = BCrypt.Net.BCrypt.HashPassword(model.Password),
            Papel = "ROLE_ALUNO",
            DataCriacao = DateTime.UtcNow
        };

        // Salva diretamente no banco de dados
        _context.Users.Add(user);
        await _context.SaveChangesAsync();

        return Ok("Usuário criado com sucesso");
    }

    // =========================
    // PERFIL (PROTEGIDO)
    // =========================
    [Authorize]
    [HttpGet("perfil")]
    public IActionResult Perfil()
    {
        var email = User.Identity?.Name;
        var role = User.FindFirst(System.Security.Claims.ClaimTypes.Role)?.Value;

        return Ok(new
        {
            message = "Acesso autorizado",
            user = email,
            papel = role
        });
    }
}
