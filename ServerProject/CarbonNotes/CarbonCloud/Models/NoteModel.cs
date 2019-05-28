using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Web;

namespace CarbonNotes.Models
{
    public class NoteModel
    {
        public int Id { get; set; }
        public string UserId { get; set; }
        public string NoteName { get; set; }
        public string NoteText { get; set; }

    }
    public class NoteDbContext : DbContext
    {
        public NoteDbContext()
            : base("DefaultConnection")
        {
        }

        public DbSet<NoteModel> Notes { get; set; }
    }
}