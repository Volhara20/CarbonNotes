using CarbonCloud.Models;
using CarbonCloud.Providers;
using CarbonNotes.Models;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;

namespace CarbonCloud.Controllers
{
    [Authorize]
    public class ValuesController : ApiController
    {
        private ApplicationUserManager _userManager;
        public ApplicationUserManager UserManager
        {
            get
            {
                return _userManager ?? Request.GetOwinContext().GetUserManager<ApplicationUserManager>();
            }
            private set
            {
                _userManager = value;
            }
        }
        // GET api/values
        [Authorize]
        public NoteGetModel Get()
        {
            try
            {
                return NotesProvider.GetAll(User.Identity.GetUserId());
            } catch
            {
                return null;
            }
        }
        // GET api/values/CreateNote
        [HttpGet]
        [Authorize]
        [Route("api/values/CreateNote")]
        public void CreateNote(string NoteName, string NoteText)
        {
            NotesProvider.CreateNote(User.Identity.GetUserId(), NoteName, NoteText);
        }
        // POST api/values/DeleteAll
        [HttpPost]
        [Authorize]
        [Route("api/values/DeleteAll")]
        public void DeleteAll([FromBody]bool confirm)
        {
            if (!confirm)
            {
                NotesProvider.DeleteAll(User.Identity.GetUserId());
            }
        }
        // POST api/values/Rename
        [HttpPost]
        [Authorize]
        [Route("api/values/Rename")]
        public void Rename(EditTitleModel edit)
        {
            NotesProvider.RenameNote(User.Identity.GetUserId(), edit.oldtitle, edit.newtitle);
        }

        // POST api/values/post
        [HttpPost]
        [Authorize]
        [Route("api/values/post")]
        public void Post(EditNoteModel edit)
        {
            NotesProvider.EditNote(User.Identity.GetUserId(), edit.name, edit.oldtext, edit.newtext);
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }
        [Authorize]
        // DELETE api/values/5
        public void Delete(string noteName)
        {
            NotesProvider.DeleteNote(User.Identity.GetUserId(),noteName);
        }
    }
}
