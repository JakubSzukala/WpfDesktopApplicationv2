using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Text;

namespace WpfDesktopApplicationv2.ViewModels
{
    class ParentViewModel
    {
        public ObservableCollection<object> children { set; get; }

        public ParentViewModel()
        {
            children = new ObservableCollection<object>();
            children.Add(new DataDisplayViewModel());
            children.Add(new LedControlViewModel());
        }
    }
}
