using System;
using System.Collections.Generic;
using System.Text;

namespace WpfDesktopApplicationv2.Models
{
    public class Measurement3dModel
    {
        public string Name { set; get; }
        public Dictionary<string, string> Data { set; get; }
        public string Unit { set; get; }
    }
}
