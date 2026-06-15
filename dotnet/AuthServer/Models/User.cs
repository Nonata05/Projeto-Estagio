
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace AuthServer.Models;


public class User
{
  
    [Key]
    [Column("id")]
    public int Id { get; set; }

    [Column("nome")]
    public required string Nome { get; set; }

    [Column("email")]
    public required string Email { get; set; }

    [Column("senha")]
    public required string Senha { get; set; }

    [Column("papel")]
    public required string Papel { get; set; }

    [Column("data_criacao")]
    public DateTime DataCriacao { get; set; } = DateTime.UtcNow;
}