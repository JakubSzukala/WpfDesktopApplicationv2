using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;
using WpfDesktopApplicationv2.Models;

namespace WpfDesktopApplicationv2.ViewModels
{
    class DataDisplayViewModel
    {
        //properties
        public ObservableCollection<MeasurementViewModel> measurements { get; set; }

        
        //fields
        private readonly ServerIoTmock _server;


        public DataDisplayViewModel()
        {
            _server = new ServerIoTmock();
        }

        private void GetData()
        {
            
        }
    }
}
