using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CarbonNotes.Models
{
    public class EditNoteModel
    {
        public string name { get; set; }
        public string newtext { get; set; }
        public string oldtext { get; set; }
    }
    public class EditTitleModel
    {
        public string oldtitle { get; set; }
        public string newtitle { get; set; }
    }
}